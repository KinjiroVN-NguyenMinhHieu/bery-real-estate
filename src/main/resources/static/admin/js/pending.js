"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
const gLOGIN_URL = "../login.html";
const gHOME_URL = "../index.html";

//URL API
const gBASE_URL = window.location.origin;
const gAUTH_URL = "/auth";
const URL_API_GET_PROVINCES = "/provinces";
const URL_API_DISTRICTS_BY_PROVINCE_ID =
  "/province/districts?provinceId=";
const URL_API_WARDS_BY_DISTRICT_ID =
  "/district/wards?districtId=";
const URL_API_STREETS_BY_DISTRICT_ID =
  "/district/streets?districtId=";

//Biến lưu trữ thông tin
let gNumberOfElements = 10; //số bản ghi filter 1 page
let gTotalElements; //tổng số bản ghi
let gTotalPages; //tổng số page
let gCurrentPage; 
let gId;

//Biến lưu trạng thái
let gStatus;
const gStatusEnum = {
  APPROVED: 'approved',
  REJECTED: 'rejected',
};

// Biến mảng toàn cục chứa danh sách tên các thuộc tính
const gCOLS = [
  "stt",
  "title",
  "type",
  "request",
  "price",
  "acreage",
  "projectName",
  "address",
  "status",
  "employeeUserName",
  "createdByUserName",
  "createdAt",
  "action",
];

// Biến toàn cục định nghĩa chỉ số các cột tương ứng
const gSTT_COL = 0;
const gTITLE_COL = 1;
const gTYPE_COL = 2;
const gREQUEST_COL = 3;
const gPRICE_COL = 4;
const gACREAGE_COL = 5;
const gPROJECT_COL = 6;
const gADDRESS_COL = 7;
const gSTATUS_COL = 8;
const gEMPLOYEE_COL = 9;
const gCREATED_BY_COL = 10;
const gCREATED_AT_COL = 11;
const gACTION_COL = 12;

//định nghĩa table
var gTable = $("#main-table").DataTable({
  dom: "Bfrtip", // B: Buttons, f: filtering input, r: processing display element, t: table, i: table information summary, p: pagination control
  buttons: ["copy", "csv", "excel", "pdf", "print", "colvis"],
  paging: false,
  columns: [
    { data: gCOLS[gSTT_COL] },
    { data: gCOLS[gTITLE_COL] },
    { data: gCOLS[gTYPE_COL] },
    { data: gCOLS[gREQUEST_COL] },
    { data: gCOLS[gPRICE_COL] },
    { data: gCOLS[gACREAGE_COL] },
    { data: gCOLS[gPROJECT_COL] },
    { data: gCOLS[gADDRESS_COL] },
    { data: gCOLS[gSTATUS_COL] },
    { data: gCOLS[gEMPLOYEE_COL] },
    { data: gCOLS[gCREATED_BY_COL] },
    { data: gCOLS[gCREATED_AT_COL] },
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
          <i class="fas fa-check-circle approve-icon btn text-primary mx-auto"></i>
          <i class="fas fa-times-circle reject-icon btn text-danger mx-auto"></i>
        </div>
      `,
    },
  ],
});

/*** REGION 2 - Vùng gán / thực thi hàm xử lý sự kiện cho các elements */
$(document).ready(function () {
  onPageLoading();

  //Event click icon resolve
  $("#main-table").on("click", ".approve-icon", function () {
    gStatus = gStatusEnum.APPROVED;
    $("#main-modal span").eq(0).html("Approve");
    $("#main-modal .modal-body form").removeClass("d-none");
    $("#delete-body").addClass("d-none");
    $("#btn-confirm").removeClass("btn-success").addClass("btn-primary");
    $("#main-modal").modal("show");
    onIconApproveClick(this);
  });

  //Event click icon delete
  $("#main-table").on("click", ".reject-icon", function () {
    gStatus = gStatusEnum.REJECTED;
    collectIdRowClick(this);
    $("#main-modal span").eq(0).html("Reject");
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
    $("#file-name-display").html("");
    $("#demo-photos").html("");
  });

  //sự kiện thay đổi select province
  $("#select-province").on("change", function () {
    //Lấy provinceId tương ứng province đang đc lựa chọn
    let provinceId = $(this).val();
    if (provinceId !== "") {
      //call API
      callAPIGetDistrictsByProvinceId(provinceId);
    }
  });

  //sự kiện thay đổi select district
  $("#select-district").on("change", function () {
    //Lấy districtId tương ứng district đang đc lựa chọn
    let districtId = $(this).val();
    if (districtId !== "") {
      //call API
      callAPIGetWardsByDistrictId(districtId);
      callAPIGetStreetsByDistrictId(districtId);
      callAPIGetProjectsByDistrictId(districtId);
    }
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
  callAPIGetAllProvinces();
}

// Hàm tạo trang
function createPage(paramPagenum) {
  const vQueryParamsObj = {
    page: paramPagenum - 1,
    size: gNumberOfElements,
  };
  callAPIGetAllRealEstatesByStatus(vQueryParamsObj); 
}

// Hàm xử lý sự kiện click icon update
function onIconApproveClick(paramIcon) {
  //B1: Thu thập dữ liệu Id
  collectIdRowClick(paramIcon);
  //B3: call API get by ID
  callAPIGetRealEstateById(gId);
}

// Khai báo hàm xử lý sự kiện click confirm
function onBtnConfirm() {
  const accessToken = getAccessToken();
  if (gStatus === gStatusEnum.APPROVED) {
    callAPIApproveProperty(gId, accessToken);
    return;
  }
  callAPIRejectProperty(gId, accessToken);
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//Call API
function callAPIGetAllRealEstatesByStatus(paramQueryObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + getAccessToken()
  };
  const vQueryParams = new URLSearchParams(paramQueryObj);
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/pending?" + vQueryParams.toString(),
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

function callAPIGetAllProvinces() {
  blockUI();
  $.ajax({
    url: URL_API_GET_PROVINCES,
    method: "GET",
    success: function (res) {
      loadDataToProvinceSelect(res);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPIGetRealEstateById(paramId) {
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/" + paramId,
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

function callAPIGetDistrictsByProvinceId(provinceId) {
  return new Promise((resolve, reject) => {
    blockUI();
    $.ajax({
      url: URL_API_DISTRICTS_BY_PROVINCE_ID + provinceId,
      method: "GET",
      success: function (res) {
        loadDataToDistrictSelect(res);
        resolve(res);
      },
      error: function (error) {
        handleError(error);
        reject(error);
      },
      finally: unblockUI(),
    });
  });
}

function callAPIGetWardsByDistrictId(districtId) {
  return new Promise((resolve, reject) => {
    blockUI();
    $.ajax({
      url: URL_API_WARDS_BY_DISTRICT_ID + districtId,
      method: "GET",
      success: function (res) {
        loadDataToWardSelect(res);
        resolve(res);
      },
      error: function (error) {
        handleError(error);
        reject(error);
      },
      finally: unblockUI(),
    });
  });
}

function callAPIGetStreetsByDistrictId(districtId) {
  return new Promise((resolve, reject) => {
    blockUI();
    $.ajax({
      url: URL_API_STREETS_BY_DISTRICT_ID + districtId,
      method: "GET",
      success: function (res) {
        loadDataToStreetSelect(res);
        resolve(res);
      },
      error: function (error) {
        handleError(error);
        reject(error);
      },
      finally: unblockUI(),
    });
  });
}

function callAPIGetProjectsByDistrictId(districtId) {
  return new Promise((resolve, reject) => {
    blockUI();
    $.ajax({
      url: gBASE_URL + "/district/" + districtId + "/projects",
      method: "GET",
      success: function (res) {
        loadDataToProjectSelect(res);
        resolve(res);
      },
      error: function (error) {
        handleError(error);
        reject(error);
      },
      finally: unblockUI(),
    });
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

//api approve
function callAPIApproveProperty(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/approve/" + paramId,
    method: "POST",
    headers: headers,
    success: function (res) {
      showToast(1, "Approve Property successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

//api reject
function callAPIRejectProperty(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/reject/" + paramId,
    method: "POST",
    headers: headers,
    success: function (res) {
      showToast(1, "Reject Property successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
      handleError(error);
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

//Load data to select
function loadDataToProvinceSelect(provinces) {
  for (let i = 0; i < provinces.length; i++) {
    let option = $("<option/>");
    option.prop("value", provinces[i].id);
    option.prop("text", provinces[i].name);
    $("#select-province").append(option);
  }
}

function loadDataToDistrictSelect(districts) {
  $("#select-district").html(""); //xóa trắng
  //thêm option selected
  $("#select-district").append(
    $('<option value="" selected>Select District</option>')
  );
  if (districts.length > 0) {
    for (let i = 0; i < districts.length; i++) {
      let option = $("<option/>");
      option.prop("value", districts[i].id);
      option.prop("text", districts[i].name);
      $("#select-district").append(option);
    }
  }
}

function loadDataToWardSelect(wards) {
  $("#select-ward").html(""); //xóa trắng
  //thêm option selected
  $("#select-ward").append(
    $('<option value="" selected>Select Ward</option>')
  );
  if (wards.length > 0) {
    for (let i = 0; i < wards.length; i++) {
      let option = $("<option/>");
      option.prop("value", wards[i].id);
      option.prop("text", wards[i].name);
      $("#select-ward").append(option);
    }
  }
}

function loadDataToStreetSelect(streets) {
  $("#select-street").html(""); //xóa trắng
  //thêm option selected
  $("#select-street").append(
    $('<option value="" selected>Select Street</option>')
  );
  if (streets.length > 0) {
    for (let i = 0; i < streets.length; i++) {
      let option = $("<option/>");
      option.prop("value", streets[i].id);
      option.prop("text", streets[i].name);
      $("#select-street").append(option);
    }
  }
}

function loadDataToProjectSelect(projects) {
  $("#select-project").html(""); //xóa trắng
  //thêm option selected
  $("#select-project").append(
    $('<option value="" selected>Select Project</option>')
  );
  if (projects.length > 0) {
    for (let i = 0; i < projects.length; i++) {
      let option = $("<option/>");
      option.prop("value", projects[i].id);
      option.prop("text", projects[i].name);
      $("#select-project").append(option);
    }
  }
}

function handleLoadDataToForm(paramData) {
  $("#input-title").val(paramData.title);
  $("#input-address").val(paramData.address);
  $("#input-title").val(paramData.title);
  $("#input-apart-code").val(paramData.apartCode);
  $("#input-description").val(paramData.description);
  $("#input-price").val(paramData.price);
  $("#input-acreage").val(paramData.acreage);
  $("#input-bedroom").val(paramData.bedroom);
  $("#input-price-rent").val(paramData.priceRent);
  $("#input-employee").val(paramData.employeeId);
  $("#input-created-by").val(paramData.createdByUserName);
  $("#input-updated-by").val(paramData.updatedByUserName);

  let getCreatedAt = new Date(convertDateFormat(paramData.createdAt));
  $("#input-created-at").val(getCreatedAt.toLocaleDateString("en-CA")); //en-CA có dạng yyyy-MM-dd
  let getUpdatedAt = new Date(convertDateFormat(paramData.updatedAt));
  $("#input-updated-at").val(getUpdatedAt.toLocaleDateString("en-CA")); //en-CA có dạng yyyy-MM-dd

  $("#select-type").val(paramData.type);
  $("#select-request").val(paramData.request);
  $("#select-direction").val(paramData.direction);
  $("#select-furniture-type").val(paramData.furnitureType);
  $("#select-status").val(paramData.status);
  $("#select-deleted").val(String(paramData.deleted));

  handleLoadDataCallAPIBackEnd(paramData);
  $("#demo-photos").append(createCarousel(paramData));
}

//Hàm xử lý đổ dữ liệu vào các select lấy từ BE
async function handleLoadDataCallAPIBackEnd(paramData) {
  $("#select-province").val(paramData.provinceId);
  const provinceId = $("#select-province").val();
  if (provinceId !== "") {
    await callAPIGetDistrictsByProvinceId(provinceId);
    $("#select-district").val(paramData.districtId);
    const districtId = $("#select-district").val();
    if (districtId !== "") {
      $.when(
        callAPIGetWardsByDistrictId(districtId),
        callAPIGetStreetsByDistrictId(districtId),
        callAPIGetProjectsByDistrictId(districtId)
      ).done(function(wardsResponse, streetsResponse, projectsResponse) {
        $("#select-ward").val(paramData.wardId);
        $("#select-street").val(paramData.streetId);
        $("#select-project").val(paramData.projectId);
      });
    }    
  }
}

// Hàm tạo carousel từ dữ liệu đầu vào
function createCarousel(paramData) {
  // Chuỗi HTML cho carousel
  let carouselHTML = `
    <div id="carousel-property-image" class="carousel slide" data-ride="carousel">
      <!-- Chỉ số -->
      <ol class="carousel-indicators">
  `;

  // Thêm các chỉ số vào chuỗi HTML
  paramData.photosUrl.forEach((photoUrl, index) => {
    carouselHTML += `
      <!-- Nút chỉ số cho slide ${index} -->
      <li data-target="#carousel-property-image" data-slide-to="${index}" ${index === 0 ? 'class="active"' : ''}></li>
    `;
  });

  carouselHTML += `
      </ol> <!-- /carousel-indicators -->
      
      <!-- Các slide -->
      <div class="carousel-inner">
  `;

  // Thêm các slide vào chuỗi HTML
  paramData.photosUrl.forEach((photoUrl, index) => {
    carouselHTML += `
      <!-- Slide ${index} -->
      <div class="carousel-item ${index === 0 ? 'active' : ''}">
        <img src="${photoUrl}" class="d-block w-100" alt="Estate Image">
      </div>
    `;
  });

  carouselHTML += `
      </div> <!-- /carousel-inner -->

      <!-- Nút điều khiển Trước và Sau -->
      <a class="carousel-control-prev" href="#carousel-property-image" role="button" data-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="sr-only">Previous</span>
      </a>
      <a class="carousel-control-next" href="#carousel-property-image" role="button" data-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="sr-only">Next</span>
      </a>
    </div> <!-- /carousel -->
  `;

  // Trả về chuỗi HTML của carousel
  return carouselHTML;
}

// Hàm thu thập thông tin về ID của row mà icon đc click
function collectIdRowClick(paramIcon) {
  let vRowClick = $(paramIcon).closest("tr");
  let vRowData = gTable.row(vRowClick).data();
  gId = vRowData.id;
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