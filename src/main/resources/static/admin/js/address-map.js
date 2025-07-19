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
  "address",
  "latitude",
  "longitude",
  "action",
];

// Biến toàn cục định nghĩa chỉ số các cột tương ứng
const gSTT_COL = 0;
const gADDRESS_COL = 1;
const gLATITUDE_COL = 2;
const gLONGTIDUDE_COL = 3;
const gACTION_COL = 4;

//định nghĩa table
var gTable = $("#main-table").DataTable({
  dom: "Bfrtip", // B: Buttons, f: filtering input, r: processing display element, t: table, i: table information summary, p: pagination control
  buttons: ["copy", "csv", "excel", "pdf", "print", "colvis"],
  paging: false,
  columns: [
    { data: gCOLS[gSTT_COL] },
    { data: gCOLS[gADDRESS_COL] },
    { data: gCOLS[gLATITUDE_COL] },
    { data: gCOLS[gLONGTIDUDE_COL] },
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
  });

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
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
  callAPIGetAllAddress(vQueryParamsObj); 
}

// Hàm xử lý sự kiện click icon update
function onIconApproveClick(paramIcon) {
  //B1: Thu thập dữ liệu Id
  collectIdRowClick(paramIcon);
  const accessToken = getAccessToken();
  //B3: call API get by ID
  callAPIGetAddressById(accessToken, gId);
}

// Khai báo hàm xử lý sự kiện click confirm
function onBtnConfirm() {
  const accessToken = getAccessToken();
  if (gStatus === gStatusEnum.DELETE) {
    callAPIDeleteAddress(gId, accessToken);
    return;
  }

  //B0: Tạo object
  let vDataObj = {
    address: "",          
    latitude: null,      
    longitude: null,          
  };
  
  //B1: Thu thập
  collectDataSubmit(vDataObj);
  //B2: Validation
  let vCheck = validateDataSubmit(vDataObj);
  //B3: Call Api
  if (vCheck) {
    switch (gStatus) {
      case gStatusEnum.CREATE:
        callAPICreateAddress(accessToken, vDataObj);
        break;
      case gStatusEnum.UPDATE:
        callAPIUpdateAddress(accessToken, gId, vDataObj);
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
function callAPIGetAllAddress(paramQueryObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + getAccessToken(),
  };
  const vQueryParams = new URLSearchParams(paramQueryObj);

  $.ajax({
    url: gBASE_URL + "/address-maps?" + vQueryParams.toString(),
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
  });
}

function callAPIGetAddressById(paramAccessToken, paramId) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    url: gBASE_URL + "/address-maps/" + paramId,
    headers: headers,
    method: "GET",
    success: function (res) {
      handleLoadDataToForm(res);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPICreateAddress(paramAccessToken, paramDataObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    type: "POST",
    headers: headers,
    url: gBASE_URL + "/address-maps",
    contentType: "application/json",
    data: JSON.stringify(paramDataObj),
    success: function (paramData) {
      showToast(1, "Create Address successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIUpdateAddress(paramAccessToken, paramId, paramDataObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    type: "PUT",
    headers: headers,
    url: gBASE_URL + `/address-maps/` + paramId,
    contentType: "application/json",
    data: JSON.stringify(paramDataObj),
    success: function (paramData) {
      showToast(1, "Update Address successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIDeleteAddress(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    url: gBASE_URL + "/address-maps/" + paramId,
    method: "DELETE",
    headers: headers,
    success: function (res) {
      $("#main-modal").modal("hide");
      showToast(1, "Delete Address successfully!");
      createPage(1);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

//api verify admin
function callAPIVerifyAdmin(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

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
      }
  });
}

//api xác thực ng dùng
function callAPIVerifyUser(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

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
      }
  });
}

//api logout
function callAPILogout(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
      url: gAUTH_URL + "/logout",
      method: "PUT",
      headers: headers,
      success: function(paramData) {
        resetLogin();
      },
      error: function(error) {
        handleError(error);
      }
  });
}

//handle function
function loadDataToTable(paramResponse) {
  gTable.clear();
  gTable.rows.add(paramResponse);
  gTable.draw();
}

function handleLoadDataToForm(paramData) {
  $("#input-address").val(paramData.address); 
  $("#input-latitude").val(paramData.latitude);
  $("#input-longitude").val(paramData.longitude);
}

// Hàm thu thập thông tin về ID của row mà icon đc click
function collectIdRowClick(paramIcon) {
  let vRowClick = $(paramIcon).closest("tr");
  let vRowData = gTable.row(vRowClick).data();
  gId = vRowData.id;
}

function collectDataSubmit(paramObj) {
  paramObj.address = $.trim($("#input-address").val());
  
  let latitudeInput = $.trim($("#input-latitude").val());
  paramObj.latitude = latitudeInput !== "" ? parseFloat(latitudeInput) : null;
  
  let longitudeInput = $.trim($("#input-longitude").val());
  paramObj.longitude = longitudeInput !== "" ? parseFloat(longitudeInput) : null;
}

// Hàm validation
function validateDataSubmit(paramObj) {
  if (!paramObj.address || paramObj.address.trim() === "") {
    showToast(3, "Address is invalid");
    return false;
  }
  if (!paramObj.latitude) {
    showToast(3, "Latitude is invalid");
    return false;
  }
  if (!paramObj.longitude) {
    showToast(3, "Longitude is invalid");
    return false;
  }
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

// Hàm hiển thị thông báo
function showToast(paramType, paramMessage) {
  switch (paramType) {
    case 1: //success
      toastr.success(paramMessage);
      break;
    case 2: //info
      toastr.info(paramMessage);
      break;
    case 3: //error
      toastr.error(paramMessage);
      break;
    case 4: //warning
      toastr.warning(paramMessage);
      break;
  }
}

function handleError(error) {
  try {
    const responseObject = JSON.parse(error.responseText);
    showToast(3, responseObject.message);
  } catch (e) {
    showToast(3, error.responseText || error.statusText);
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