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
  CREATE: 'create',
  UPDATE: 'update',
  DELETE: 'delete'
};

// Biến mảng toàn cục chứa danh sách tên các thuộc tính
const gCOLS = [
  "stt",
  "name",
  "address",
  "acreage",
  "numBlock",
  "numFloors",
  "numApartment",
  "investorName",
  "constructionContractorName",
  "designUnitName",
  "deleted",
  "action",
];

// Biến toàn cục định nghĩa chỉ số các cột tương ứng
const gSTT_COL = 0;
const gNAME_COL = 1;
const gADDRESS_COL = 2;
const gACREAGE_COL = 3;
const gBLOCK_COL = 4;
const gFLOORS_COL = 5;
const gAPARTMENT_COL = 6;
const gINVESTOR_COL = 7;
const gCONSTRUCTION_CONTRACTOR_COL = 8;
const gDESIGN_UNIT_COL = 9;
const gDELETED_COL = 10;
const gACTION_COL = 11;

//định nghĩa table
var gTable = $("#main-table").DataTable({
  dom: "Bfrtip", // B: Buttons, f: filtering input, r: processing display element, t: table, i: table information summary, p: pagination control
  buttons: ["copy", "csv", "excel", "pdf", "print", "colvis"],
  paging: false,
  columns: [
    { data: gCOLS[gSTT_COL] },
    { data: gCOLS[gNAME_COL] },
    { data: gCOLS[gADDRESS_COL] },
    { data: gCOLS[gACREAGE_COL] },
    { data: gCOLS[gBLOCK_COL] },
    { data: gCOLS[gFLOORS_COL] },
    { data: gCOLS[gAPARTMENT_COL] },
    { data: gCOLS[gINVESTOR_COL] },
    { data: gCOLS[gCONSTRUCTION_CONTRACTOR_COL] },
    { data: gCOLS[gDESIGN_UNIT_COL] },
    { data: gCOLS[gDELETED_COL] },
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
    $("#select-district").prop("disabled", true);
    $("#select-ward").prop("disabled", true);
    $("#select-street").prop("disabled", true);
  });

  //sự kiện thay đổi select province
  $("#select-province").on("change", function () {
    //reset select district+ward+street+project để tránh ng dùng bị nhầm lẫn dữ liệu trong khi chờ callAPI
    $("#select-district").val("").prop("disabled", true);
    $("#select-ward").val("").prop("disabled", true);
    $("#select-street").val("").prop("disabled", true);
    $("#select-project").val("").prop("disabled", true);
    //Lấy provinceId tương ứng province đang đc lựa chọn
    let provinceId = $(this).val();
    if (provinceId !== "") {
      //call API
      callAPIGetDistrictsByProvinceId(provinceId);
    }
  });

  //sự kiện thay đổi select district
  $("#select-district").on("change", function () {
    //reset select ward+street+project để tránh ng dùng bị nhầm lẫn dữ liệu trong khi chờ callAPI
    $("#select-ward").val("").prop("disabled", true);
    $("#select-street").val("").prop("disabled", true);
    $("#select-project").val("").prop("disabled", true);
    //Lấy districtId tương ứng district đang đc lựa chọn
    let districtId = $(this).val();
    if (districtId !== "") {
      //call API
      callAPIGetWardsByDistrictId(districtId);
      callAPIGetStreetsByDistrictId(districtId);
    }
  });

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });

  // kích hoạt select2
  $('.select2').select2({
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
  callAPIGetAllInvestor(accessToken);
  callAPIGetAllContractor(accessToken);
  callAPIGetAllDesignUnit(accessToken);
  callAPIGetAllUtilities(accessToken);
  callAPIGetAllRegionLinks(accessToken);
  callAPIGetAllProvinces();
}

// Hàm tạo trang
function createPage(paramPagenum) {
  const vQueryParamsObj = {
    page: paramPagenum - 1,
    size: gNumberOfElements,
  };
  callAPIGetAllProjects(vQueryParamsObj); 
}

// Hàm xử lý sự kiện click icon update
function onIconApproveClick(paramIcon) {
  //B1: Thu thập dữ liệu Id
  collectIdRowClick(paramIcon);
  const accessToken = getAccessToken();
  //B3: call API get by ID
  callAPIGetInvestorById(accessToken, gId);
}

// Khai báo hàm xử lý sự kiện click confirm
function onBtnConfirm() {
  const accessToken = getAccessToken();
  if (gStatus === gStatusEnum.DELETE) {
    callAPIDeleteInvestor(gId, accessToken);
    return;
  }

  //B0: Tạo object
  let vProjectObj = {
    name: "",
    provinceId: null,
    districtId: null,
    wardId: 0,
    streetId: 0,
    address: "",
    slogan: "",
    description: "",
    acreage: null,
    constructArea: null,
    numBlock: null,
    numFloors: "",
    numApartment: 0,
    apartmenttArea: "",
    investorId: null,
    constructionContractorId: 0,
    designUnitId: null,
    photo: "",
    latitude: null,
    longitude: null,
    isDeleted: false,
    utilities: [],
    regionLinks: [],
  };  
  
  //B1: Thu thập
  collectDataSubmit(vProjectObj);
  //B2: Validation
  let vCheck = validateDataSubmit(vProjectObj);
  //B3: Call Api
  if (vCheck) {
    switch (gStatus) {
      case gStatusEnum.CREATE:
        callAPICreateInvestor(accessToken, vProjectObj);
        break;
      case gStatusEnum.UPDATE:
        callAPIUpdateInvestor(accessToken, gId, vProjectObj);
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
function callAPIGetAllProjects(paramQueryObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + getAccessToken()
  };
  const vQueryParams = new URLSearchParams(paramQueryObj);

  $.ajax({
    url: gBASE_URL + "/projects?" + vQueryParams.toString(),
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

function callAPIGetAllUtilities(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    url: gBASE_URL + "/utilities/all",
    headers: headers,
    method: "GET",
    success: function (paramData) {
      // Thêm các tùy chọn mới vào Select2
      paramData.forEach(function(item) {
        var newOption = new Option(item.name, item.id);
        $('#select-utilities').append(newOption).trigger('change');
      });
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIGetAllRegionLinks(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    url: gBASE_URL + "/region-links/all",
    headers: headers,
    method: "GET",
    success: function (paramData) {
      // Thêm các tùy chọn mới vào Select2
      paramData.forEach(function(item) {
        var newOption = new Option(item.name, item.id);
        $('#select-region').append(newOption).trigger('change');
      });
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIGetAllInvestor(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  $.ajax({
    url: gBASE_URL + "/investor/all",
    headers: headers,
    method: "GET",
    success: function (res) {
      loadDataToInvestorSelect(res);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIGetAllContractor(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  $.ajax({
    url: gBASE_URL + "/construction-contractors/all",
    headers: headers,
    method: "GET",
    success: function (res) {
      loadDataToContractorSelect(res);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIGetAllDesignUnit(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  $.ajax({
    url: gBASE_URL + "/design-units/all",
    headers: headers,
    method: "GET",
    success: function (res) {
      loadDataToDesignUnitSelect(res);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIGetAllProvinces() {
  $.ajax({
    url: URL_API_GET_PROVINCES,
    method: "GET",
    success: function (res) {
      loadDataToProvinceSelect(res);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIGetDistrictsByProvinceId(provinceId) {
  return new Promise((resolve, reject) => {
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
    });
  });
}

function callAPIGetWardsByDistrictId(districtId) {
  return new Promise((resolve, reject) => {
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
    });
  });
}

function callAPIGetStreetsByDistrictId(districtId) {
  return new Promise((resolve, reject) => {
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
    });
  });
}

function callAPIGetInvestorById(paramAccessToken, paramId) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    url: gBASE_URL + "/projects/" + paramId,
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

function callAPICreateInvestor(paramAccessToken, paramDataObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    type: "POST",
    headers: headers,
    url: gBASE_URL + "/projects",
    contentType: "application/json",
    data: JSON.stringify(paramDataObj),
    success: function (paramData) {
      showToast(1, "Create Project successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIUpdateInvestor(paramAccessToken, paramId, paramDataObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    type: "PUT",
    headers: headers,
    url: gBASE_URL + "/projects/" + paramId,
    contentType: "application/json",
    data: JSON.stringify(paramDataObj),
    success: function (paramData) {
      showToast(1, "Update Project successfully");
      $('#main-modal').modal('hide');
      createPage(1);
    },
    error: function(error) {
        handleError(error);
      },
  });
}

function callAPIDeleteInvestor(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    url: gBASE_URL + "/projects/" + paramId,
    method: "DELETE",
    headers: headers,
    success: function (res) {
      $("#main-modal").modal("hide");
      showToast(1, "Delete Project successfully!");
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
  $("#input-name").val(paramData.name);
  $("#input-address").val(paramData.address);
  $("#input-slogan").val(paramData.slogan);
  $("#input-description").val(paramData.description);
  $("#input-acreage").val(paramData.acreage ? paramData.acreage.toString() : '');
  $("#input-construct").val(paramData.constructArea ? paramData.constructArea.toString() : '');
  $("#input-block").val(paramData.numBlock ? paramData.numBlock.toString() : '');
  $("#input-floor").val(paramData.numFloors);
  $("#input-apartment").val(paramData.numApartment ? paramData.numApartment.toString() : '');
  $("#input-apartment-area").val(paramData.apartmenttArea);
  $("#select-investor").val(paramData.investorId);
  $("#select-contractor").val(paramData.constructionContractorId);
  $("#select-design-unit").val(paramData.designUnitId);
  $("#input-photo").val(paramData.photo);
  $("#input-latitude").val(paramData.latitude ? paramData.latitude.toString() : '');
  $("#input-longitude").val(paramData.longitude ? paramData.longitude.toString() : '');
  $("#select-deleted").val(paramData.deleted ? 'true' : 'false');

  // Tạo một mảng chứa các name của paramData.utilities, region links
  let selectedUtilities = paramData.utilities.map(utility => utility.id);
  let selectedRegionLinks = paramData.regionLinks.map(region => region.id);

  // Chọn các giá trị dựa trên dữ liệu từ server
  $('#select-utilities').val(selectedUtilities).trigger('change');
  $('#select-region').val(selectedRegionLinks).trigger('change');
  handleLoadDataCallAPIBackEnd(paramData);
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
      ).done(function(wardsResponse, streetsResponse, projectsResponse) {
        $("#select-ward").val(paramData.wardId);
        $("#select-street").val(paramData.streetId);
        if (gStatus === gStatusEnum.DELETE) {
          $("#main-modal form .form-control").prop("disabled", true);
        }
      });
    }    
  }
}

//Load data to select
function loadDataToInvestorSelect(data) {
  for (let i = 0; i < data.length; i++) {
    let option = $("<option/>");
    option.prop("value", data[i].id);
    option.prop("text", data[i].name);
    $("#select-investor").append(option);
  }
}

function loadDataToContractorSelect(data) {
  for (let i = 0; i < data.length; i++) {
    let option = $("<option/>");
    option.prop("value", data[i].id);
    option.prop("text", data[i].name);
    $("#select-contractor").append(option);
  }
}

function loadDataToDesignUnitSelect(data) {
  for (let i = 0; i < data.length; i++) {
    let option = $("<option/>");
    option.prop("value", data[i].id);
    option.prop("text", data[i].name);
    $("#select-design-unit").append(option);
  }
}

function loadDataToProvinceSelect(data) {
  for (let i = 0; i < data.length; i++) {
    let option = $("<option/>");
    option.prop("value", data[i].id);
    option.prop("text", data[i].name);
    $("#select-province").append(option);
  }
}

function loadDataToDistrictSelect(data) {
  $("#select-district").html(""); //xóa trắng
  //thêm option selected
  $("#select-district").append(
    $('<option value="" selected>Select District</option>')
  );
  if (data.length > 0) {
    $("#select-district").prop("disabled", false); //bật select

    for (let i = 0; i < data.length; i++) {
      let option = $("<option/>");
      option.prop("value", data[i].id);
      option.prop("text", data[i].name);
      $("#select-district").append(option);
    }
  }
}

function loadDataToWardSelect(data) {
  $("#select-ward").html(""); //xóa trắng
  //thêm option selected
  $("#select-ward").append(
    $('<option value="" selected>Select Ward</option>')
  );
  if (data.length > 0) {
    $("#select-ward").prop("disabled", false); //bật select

    for (let i = 0; i < data.length; i++) {
      let option = $("<option/>");
      option.prop("value", data[i].id);
      option.prop("text", data[i].name);
      $("#select-ward").append(option);
    }
  }
}

function loadDataToStreetSelect(data) {
  $("#select-street").html(""); //xóa trắng
  //thêm option selected
  $("#select-street").append(
    $('<option value="" selected>Select Street</option>')
  );
  if (data.length > 0) {
    $("#select-street").prop("disabled", false); //bật select

    for (let i = 0; i < data.length; i++) {
      let option = $("<option/>");
      option.prop("value", data[i].id);
      option.prop("text", data[i].name);
      $("#select-street").append(option);
    }
  }
}

// Hàm thu thập thông tin về ID của row mà icon đc click
function collectIdRowClick(paramIcon) {
  let vRowClick = $(paramIcon).closest("tr");
  let vRowData = gTable.row(vRowClick).data();
  gId = vRowData.id;
}

//Hàm thu thập dữ liệu
function collectDataSubmit(paramProjectObj) {
  paramProjectObj.name = $.trim($("#input-name").val());
  paramProjectObj.provinceId = parseInt($("#select-province").val()) || null;
  paramProjectObj.districtId = parseInt($("#select-district").val()) || null;
  paramProjectObj.wardId = parseInt($("#select-ward").val()) || 0;
  paramProjectObj.streetId = parseInt($("#select-street").val()) || 0;
  paramProjectObj.address = $.trim($("#input-address").val());
  paramProjectObj.slogan = $.trim($("#input-slogan").val());
  paramProjectObj.description = $.trim($("#input-description").val());
  paramProjectObj.acreage = parseFloat($.trim($("#input-acreage").val())) || null;
  paramProjectObj.constructArea = parseFloat($.trim($("#input-construct").val())) || null;
  paramProjectObj.numBlock = parseInt($.trim($("#input-block").val())) || null;
  paramProjectObj.numFloors = $.trim($("#input-floor").val());
  paramProjectObj.numApartment = parseInt($.trim($("#input-apartment").val())) || 0;
  paramProjectObj.apartmenttArea = $.trim($("#input-apartment-area").val());
  paramProjectObj.investorId = parseInt($("#select-investor").val()) || null;
  paramProjectObj.constructionContractorId = parseInt($("#select-contractor").val()) || null;
  paramProjectObj.designUnitId = parseInt($("#select-design-unit").val()) || null;
  paramProjectObj.photo = $.trim($("#input-photo").val());
  paramProjectObj.latitude = parseFloat($.trim($("#input-latitude").val())) || null;
  paramProjectObj.longitude = parseFloat($.trim($("#input-longitude").val())) || null;
  paramProjectObj.isDeleted = $("#select-deleted").val() === 'false';

  // Thêm utilities và regionlinks từ select 
  const selectedUtilities = $('#select-utilities').val() || [];
  const selectedRegionLinks = $('#select-region').val() || [];
  selectedUtilities.forEach(utilityId => {
    // Kiểm tra xem đã tồn tại chưa để tránh trùng lặp
    if (!paramProjectObj.utilities.some(utility => utility.id === utilityId)) {
      paramProjectObj.utilities.push({ id: utilityId });
    }
  });
  selectedRegionLinks.forEach(regionId => {
    // Kiểm tra xem đã tồn tại chưa để tránh trùng lặp
    if (!paramProjectObj.regionLinks.some(region => region.id === regionId)) {
      paramProjectObj.regionLinks.push({ id: regionId });
    }
  });
}

// Hàm validation
function validateDataSubmit(paramProjectObj) {
  // Kiểm tra trường name
  if (!paramProjectObj.name || paramProjectObj.name.trim() === "") {
    showToast(3, "Name is invalid");
    return false;
  }

  // Kiểm tra trường provinceId
  if (!paramProjectObj.provinceId || paramProjectObj.provinceId <= 0) {
    showToast(3, "Province is invalid");
    return false;
  }

  // Kiểm tra trường districtId
  if (!paramProjectObj.districtId || paramProjectObj.districtId <= 0) {
    showToast(3, "District is invalid");
    return false;
  }

  // Kiểm tra trường address
  if (!paramProjectObj.address || paramProjectObj.address.trim() === "") {
    showToast(3, "Address is invalid");
    return false;
  }

  // Kiểm tra trường investorId
  if (!paramProjectObj.investorId || paramProjectObj.investorId <= 0) {
    showToast(3, "Investor Id is invalid");
    return false;
  }

  // Kiểm tra trường constructionContractorId
  if (!paramProjectObj.constructionContractorId || paramProjectObj.constructionContractorId <= 0) {
    showToast(3, "Contractor Id is invalid");
    return false;
  }

  // Kiểm tra trường designUnitId
  if (!paramProjectObj.designUnitId || paramProjectObj.designUnitId <= 0) {
    showToast(3, "Design Unit Id is invalid");
    return false;
  }

  // Kiểm tra trường isDeleted
  if (paramProjectObj.isDeleted === undefined || paramProjectObj.isDeleted === null) {
    showToast(3, "Deleted status is invalid");
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

//chuyển ngày dd-MM-yyyy thành yyyy-MM-dd(để trình duyệt hiểu)
function convertDateFormat(paramDateString) {
  if (paramDateString) {
    const dateParts = paramDateString.split("-");
    return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
  }
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