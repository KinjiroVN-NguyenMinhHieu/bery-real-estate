"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
const gLOGIN_URL = "../login.html";
const gHOME_URL = "../index.html";

//URL API
const gBASE_URL = window.location.origin;
const gAUTH_URL = "/auth";

//Biến lưu trữ thông tin
let gNumberOfElements = 10; //số bản ghi filter 1 page
let gTotalElements; //tổng số bản ghi
let gTotalPages; //tổng số page
let gCurrentPage; 
let gId;

//Biến lưu trạng thái
let gStatus;
const gStatusEnum = {
  CREATE: 'create',
  UPDATE: 'update',
  DELETE: 'delete'
};

// Biến mảng toàn cục chứa danh sách tên các thuộc tính
const gCOLS = [
  "stt",
  "lastName",
  "firstName",
  "userName",
  "email",
  "address",
  "photo",
  "activated",
  "createdAt",
  "updatedAt",
  "action",
];

// Biến toàn cục định nghĩa chỉ số các cột tương ứng
const gSTT_COL = 0;
const gLAST_NAME_COL = 1;
const gFIRST_NAME_COL = 2;
const gUSER_NAME_COL = 3;
const gEMAIL_COL = 4;
const gADDRESS_COL = 5;
const gPHOTO_COL = 6;
const gACTIVATED_COL = 7;
const gCREATED_AT_COL = 8;
const gUPDATED_AT_COL = 9;
const gACTION_COL = 10;

//định nghĩa table
var gTable = $("#main-table").DataTable({
  dom: "Bfrtip", // B: Buttons, f: filtering input, r: processing display element, t: table, i: table information summary, p: pagination control
  buttons: ["copy", "csv", "excel", "pdf", "print", "colvis"],
  paging: false,
  columns: [
    { data: gCOLS[gSTT_COL] },
    { data: gCOLS[gLAST_NAME_COL] },
    { data: gCOLS[gFIRST_NAME_COL] },
    { data: gCOLS[gUSER_NAME_COL] },
    { data: gCOLS[gEMAIL_COL] },
    { data: gCOLS[gADDRESS_COL] },
    { data: gCOLS[gPHOTO_COL] },
    { data: gCOLS[gACTIVATED_COL] },
    { data: gCOLS[gCREATED_AT_COL] },
    { data: gCOLS[gUPDATED_AT_COL] },
    { data: gCOLS[gACTION_COL] },
  ],
  columnDefs: [
    {
      target: gSTT_COL,
      className: "text-center",
      render: (data, type, row, meta) => {
        return meta.row + 1 + gCurrentPage * gNumberOfElements;
      },
    },
    {
      target: gPHOTO_COL,
      className: "text-center",
      render: (data, type, row, meta) => {
        return data ? `<img src="${data}" alt="Image" class="img-fluid">` : '';
      },
    },
    {
      target: gACTION_COL,
      className: "text-center",
      defaultContent: `
        <div class="d-flex justify-content-around">
          <i class="fas fa-edit update-icon btn text-primary mx-auto"></i>
          <i class="fas fa-trash-alt delete-icon btn text-danger mx-auto"></i>
        </div>
      `,
    },
  ],
});

/*** REGION 2 - Vùng gán / thực thi hàm xử lý sự kiện cho các elements */
$(document).ready(function () {
  onPageLoading();

  //Event click btn thêm mới
  $("#btn-create").click(() => {
    gStatus = gStatusEnum.CREATE;
    $("#main-modal span").eq(0).html("Create");
    $("#main-modal .modal-body form").removeClass("d-none");
    $("#delete-body").addClass("d-none");
    $("#btn-confirm").removeClass("btn-success btn-danger").addClass("btn-primary");
    $("#main-modal").modal("show");
  });

  //Event click icon update
  $("#main-table").on("click", ".update-icon", function () {
    gStatus = gStatusEnum.UPDATE;
    $("#main-modal span").eq(0).html("Update");
    $("#main-modal .modal-body form").removeClass("d-none");
    $("#delete-body").addClass("d-none");
    $("#btn-confirm").removeClass("btn-success").addClass("btn-primary");
    $("#main-modal").modal("show");
    onIconApproveClick(this);
  });

  //Event click icon delete
  $("#main-table").on("click", ".delete-icon", function () {
    gStatus = gStatusEnum.DELETE;
    collectIdRowClick(this);
    $("#main-modal span").eq(0).html("Delete");
    $("#main-modal .modal-body form").addClass("d-none");
    $("#delete-body").removeClass("d-none");
    $("#btn-confirm").removeClass("btn-success").addClass("btn-danger");
    $("#main-modal").modal("show");
  });

  //Sự kiện click nút confirm
  $("#btn-confirm").click(onBtnConfirm);

  //Event hidden modal
  $("#main-modal").on("hidden.bs.modal", function () {
    $("#main-modal form")[0].reset(); // Reset form
    $("#demo-photo").attr("src", '').addClass("d-none");
    $("#select-roles").val('').trigger('change');
  });

  //event change input photo
  $("#input-photo").change(() => {
    let photoUrl = $("#input-photo").val();
    photoUrl ? $("#demo-photo").attr("src", photoUrl).removeClass("d-none") : null;
  });

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });

  // kích hoạt select2
  $('#select-roles').select2({
    placeholder: 'Select Roles',
    allowClear: true,
    width: '100%' // Đảm bảo độ rộng khớp với thẻ ban đầu
  });
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    callAPIVerifyAdmin(accessToken);
    callAPIVerifyUser(accessToken);
  } else {
    window.location.href = gLOGIN_URL;
  }
  createPage(1);
}

// Hàm tạo trang
function createPage(paramPagenum) {
  const vQueryParamsObj = {
    page: paramPagenum - 1,
    size: gNumberOfElements,
  };
  callAPIGetAllEmployees(vQueryParamsObj); 
}

// Hàm xử lý sự kiện click icon update
function onIconApproveClick(paramIcon) {
  //B1: Thu thập dữ liệu Id
  collectIdRowClick(paramIcon);
  const accessToken = getAccessToken();
  //B3: call API get by ID
  callAPIGetEstatesById(accessToken, gId);
}

// Khai báo hàm xử lý sự kiện click confirm
function onBtnConfirm() {
  const accessToken = getAccessToken();
  if (gStatus === gStatusEnum.DELETE) {
    callAPIDeleteProject(gId, accessToken);
    return;
  }

  //B0: Tạo object
  let vEmployeeObj = {
    firstName: "",
    lastName: "",
    userName: "",
    email: "",
    address: "",
    country: "",
    city: "",
    homePhone: "",
    photo: "",
    activated: "",
    note: "",
    birthDate: "",
    roles: []
  };
  
  //B1: Thu thập
  collectDataSubmit(vEmployeeObj);
  //B2: Validation
  let vCheck = validateDataSubmit(vEmployeeObj);
  //B3: Call Api
  if (vCheck) {
    switch (gStatus) {
      case gStatusEnum.CREATE:
        callAPICreateProject(accessToken, vEmployeeObj);
        break;
      case gStatusEnum.UPDATE:
        callAPIUpdateProject(accessToken, gId, vEmployeeObj);
        break;
    }
  }
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//Call API
function callAPIGetAllEmployees(paramQueryObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + getAccessToken()
  };
  const vQueryParams = new URLSearchParams(paramQueryObj);
  blockUI();
  $.ajax({
    url: gBASE_URL + "/employees?" + vQueryParams.toString(),
    headers: headers,
    method: "GET",
    success: function (paramData) {
      gTotalElements = paramData.totalElements; 
      gTotalPages = paramData.totalPages; 
      gCurrentPage = paramQueryObj.page;
      loadDataToTable(paramData.content);
      createPagination(gCurrentPage + 1);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPIGetEstatesById(paramAccessToken, paramId) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/employees/" + paramId,
    headers: headers,
    method: "GET",
    success: async function (res) {
      handleLoadDataToForm(res);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPICreateProject(paramAccessToken, paramEmployeeObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    type: "POST",
    headers: headers,
    url: gBASE_URL + "/employees",
    contentType: "application/json",
    data: JSON.stringify(paramEmployeeObj),
    success: function (paramData) {
      showToast(1, "Create Employee successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPIUpdateProject(paramAccessToken, paramId, paramEmployeeObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    type: "PUT",
    headers: headers,
    url: gBASE_URL + "/employees/" + paramId,
    contentType: "application/json",
    data: JSON.stringify(paramEmployeeObj),
    success: function (paramData) {
      showToast(1, "Update Employee successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPIDeleteProject(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/employees/" + paramId,
    method: "DELETE",
    headers: headers,
    success: function (res) {
      $("#main-modal").modal("hide");
      showToast(1, "Delete Employee successfully!");
      createPage(1);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

//api verify admin
function callAPIVerifyAdmin(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gAUTH_URL + "/verify-admin",
    method: "GET",
    headers: headers,
    success: function(paramData) {
      if(!paramData) {
        window.location.href = gHOME_URL;
      }
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

//api xác thực ng dùng
function callAPIVerifyUser(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gAUTH_URL + "/verify",
    method: "GET",
    headers: headers,
    success: function(paramData) {
      $(".user-name").html(paramData.username);
      $("#cart-icon .badge").html(paramData.realEstatesCount);
    },
    error: function(error) {
      handleError(error);
      resetLogin();
    },
    finally: unblockUI(),
  });
}

//api logout
function callAPILogout(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gAUTH_URL + "/logout",
    method: "PUT",
    headers: headers,
    success: function(paramData) {
      resetLogin();
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

//handle function
function loadDataToTable(paramResponse) {
  gTable.clear();
  gTable.rows.add(paramResponse);
  gTable.draw();
}

function handleLoadDataToForm(paramData) {
  $("#input-first-name").val(paramData.firstName);
  $("#input-last-name").val(paramData.lastName);
  $("#input-user-name").val(paramData.userName);
  $("#input-email").val(paramData.email);
  $("#input-address").val(paramData.address);
  $("#input-country").val(paramData.country);
  $("#input-city").val(paramData.city);
  $("#input-home-phone").val(paramData.homePhone);
  $("#input-photo").val(paramData.photo).trigger('change');;
  $("#select-activated").val(paramData.activated);
  $("#input-note").val(paramData.note);

  let getBirthDate = new Date(convertDateFormat(paramData.birthDate));
  $("#input-birth-date").val(getBirthDate.toLocaleDateString("en-CA"));
  let getCreatedAt = new Date(convertDateFormat(paramData.createdAt));
  $("#input-created-at").val(getCreatedAt.toLocaleDateString("en-CA")); // en-CA có dạng yyyy-MM-dd
  let getUpdatedAt = new Date(convertDateFormat(paramData.updatedAt));
  $("#input-updated-at").val(getUpdatedAt.toLocaleDateString("en-CA")); // en-CA có dạng yyyy-MM-dd

  // Tạo một mảng chứa các name của paramData.roles
  let selectedRoles = paramData.roles.map(role => role.name);

  // Chọn các giá trị dựa trên dữ liệu từ server
  $('#select-roles').val(selectedRoles).trigger('change');
}

// Hàm thu thập thông tin về ID của row mà icon đc click
function collectIdRowClick(paramIcon) {
  let vRowClick = $(paramIcon).closest("tr");
  let vRowData = gTable.row(vRowClick).data();
  gId = vRowData.id;
}

//Hàm thu thập dữ liệu
function collectDataSubmit(paramEmployeeObj) {
  paramEmployeeObj.firstName = $.trim($("#input-first-name").val());
  paramEmployeeObj.lastName = $.trim($("#input-last-name").val());
  paramEmployeeObj.userName = $.trim($("#input-user-name").val());
  paramEmployeeObj.email = $.trim($("#input-email").val());
  paramEmployeeObj.address = $.trim($("#input-address").val());
  paramEmployeeObj.country = $.trim($("#input-country").val());
  paramEmployeeObj.city = $.trim($("#input-city").val());
  paramEmployeeObj.homePhone = $.trim($("#input-home-phone").val());
  paramEmployeeObj.photo = $("#input-photo").val();
  paramEmployeeObj.activated = $("#select-activated").val();
  paramEmployeeObj.note = $.trim($("#input-note").val());
  // Chuyển đổi chuỗi ngày và tạo đối tượng Date
  paramEmployeeObj.birthDate = new Date(convertDateFormat($.trim($("#input-birth-date").val())));

  // Thêm tên role từ select vào vEmployeeObj.roles
  const selectedRoleNames = $('#select-roles').val() || [];
  selectedRoleNames.forEach(roleName => {
    // Kiểm tra xem role đã tồn tại chưa để tránh trùng lặp
    if (!paramEmployeeObj.roles.some(role => role.name === roleName)) {
      paramEmployeeObj.roles.push({ name: roleName });
    }
  });
}

// Hàm validation
function validateDataSubmit(paramEmployeeObj) {
  // Check username
  if (!paramEmployeeObj.userName || paramEmployeeObj.userName.trim() === "") {
    showToast(3, "Username is invalid");
    return false;
  }

  // Check email
  if (!paramEmployeeObj.email || paramEmployeeObj.email.trim() === "") {
    showToast(3, "Email is invalid");
    return false;
  }

  // Check actived
  if (!paramEmployeeObj.activated || paramEmployeeObj.activated.trim() === "") {
    showToast(3, "Actived is invalid");
    return false;
  }

  // Check roles
  if (!paramEmployeeObj.roles || paramEmployeeObj.roles.length === 0) {
    showToast(3, "Roles are invalid");
    return false;
  }

  // If all validations pass, return true
  return true;
}

//Hàm lấy accesstoken
function getAccessToken() {
  let accessToken = Cookies.get('accessToken');
  if(!accessToken) {
    accessToken = sessionStorage.getItem('accessToken');
  }
  return accessToken;
}

//hàm reset login
function resetLogin() {
  Cookies.remove('accessToken');
  sessionStorage.removeItem('accessToken');
  window.location.href = gHOME_URL;
}

//chuyển ngày dd-MM-yyyy thành yyyy-MM-dd(để trình duyệt hiểu)
function convertDateFormat(paramDateString) {
  if (paramDateString) {
    const dateParts = paramDateString.split("-");
    return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
  }
}

// ham tao thanh phan trang
function createPagination(paramPagenum) {
  // Cập nhật nội dung của thanh phân trang
  let vPaginationContainer = $("#pagination-container");
  vPaginationContainer.html(""); // Xóa nội dung cũ

  // Tạo nút Previous
  if (paramPagenum == 1) {
    vPaginationContainer.append(
      "<li class='page-item disabled' id='prev-page'><span class='page-link'>Previous</span></li>"
    );
  } else {
    vPaginationContainer.append(
      "<li class='page-item' id='prev-page'><a href='javascript:void(0)' class='page-link'>Previous</a></li>"
    );
  }

  // Hiển thị các trang trong phạm vi
  if (paramPagenum <= 5) {
    //nếu số trang <=5
    for (let bI = 1; bI <= Math.min(6, gTotalPages); bI++) {
      //hiển thị tối đa 6 trang đầu tiên
      if (paramPagenum == bI) {
        //trang đang hiển thị
        vPaginationContainer.append(
          "<li class='page-item active' id='page-" +
            bI +
            "'><span class='page-link'>" +
            bI +
            "</span></li>"
        );
      } else {
        vPaginationContainer.append(
          //các trang còn lại
          "<li class='page-item' id='page-" +
            bI +
            "'><a href='javascript:void(0)' class='page-link'>" +
            bI +
            "</a></li>"
        );
      }
    }
    if (gTotalPages > 6) {
      //nếu tổng số trang > 6 thì thêm ...
      vPaginationContainer.append(
        "<li class='page-item disabled'><span class='page-link'>...</span></li>"
      );
    }
  } else if (paramPagenum >= gTotalPages - 4) {
    //Nếu số trang <= gTotalPages - 4 (5 trang cuối cùng)
    vPaginationContainer.append(
      "<li class='page-item disabled'><span class='page-link'>...</span></li>" //thêm ... ở đầu
    );
    for (let bI = Math.max(1, gTotalPages - 5); bI <= gTotalPages; bI++) {
      //Hiển thị tối đa 6 trang cuối cùng(gTotalPages - 5), 1 ở đây áp dụng đối với trường hợp vTotalPage <= 6, ko hiển thị trang âm
      if (paramPagenum == bI) {
        //trang đang hiển thị
        vPaginationContainer.append(
          "<li class='page-item active' id='page-" +
            bI +
            "'><span class='page-link'>" +
            bI +
            "</span></li>"
        );
      } else {
        vPaginationContainer.append(
          //các trang còn lại
          "<li class='page-item' id='page-" +
            bI +
            "'><a href='javascript:void(0)' class='page-link'>" +
            bI +
            "</a></li>"
        );
      }
    }
  } else {
    //các trang ở giữa
    vPaginationContainer.append(
      //thêm trang đầu và ...
      "<li class='page-item' id='page-1'><a href='javascript:void(0)' class='page-link'>1</a></li>" +
        "<li class='page-item disabled'><span class='page-link'>...</span></li>"
    );

    for (let bI = paramPagenum - 1; bI <= paramPagenum + 1; bI++) {
      //đặt trang active ở giữa, hiển thị 3 trang
      if (paramPagenum == bI) {
        //trang đang hiển thị
        vPaginationContainer.append(
          "<li class='page-item active' id='page-" +
            bI +
            "'><span class='page-link'>" +
            bI +
            "</span></li>"
        );
      } else {
        vPaginationContainer.append(
          //các trang còn lại
          "<li class='page-item' id='page-" +
            bI +
            "'><a href='javascript:void(0)' class='page-link'>" +
            bI +
            "</a></li>"
        );
      }
    }
    vPaginationContainer.append(
      //thêm ... và trang cuối
      "<li class='page-item disabled'><span class='page-link'>...</span></li>" +
        "<li class='page-item' id='page-" +
        gTotalPages +
        "'><span class='page-link'>" +
        gTotalPages +
        "</span></li>"
    );
  }

  // Tạo nút Next
  if (paramPagenum == gTotalPages) {
    vPaginationContainer.append(
      "<li class='page-item disabled' id='next-page'><span class='page-link'>Next</span></li>"
    );
  } else {
    vPaginationContainer.append(
      "<li class='page-item' id='next-page'><a href='javascript:void(0)' class='page-link'>Next</a></li>"
    );
  }
  //Vì createPage ko đc khai báo global nên ko nhét vào html trực tiếp được. Cần gán bằng sự kiện click
  $("#prev-page").click(function () {
    //gán click cho nút prev
    createPage(paramPagenum - 1);
  });

  $("#next-page").click(function () {
    //gán click cho nút next
    createPage(paramPagenum + 1);
  });

  for (let bI = 1; bI <= gTotalPages; bI++) {//dùng vòng lặp gán sự kiện cho các nút số
    $("#page-" + bI).click(function () {
      createPage(parseInt($(this).text())); // Lấy số trang từ nút bấm
    });
  }
}