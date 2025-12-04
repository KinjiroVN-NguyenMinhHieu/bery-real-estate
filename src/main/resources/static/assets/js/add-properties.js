"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa

let gLOGIN_URL = "login.html";
let gHOME_URL = "index.html";

//URL API
let gBASE_URL = window.location.origin;
let gAUTH_URL = "/auth";
var URL_API_GET_PROVINCES = "/provinces";
var URL_API_DISTRICTS_BY_PROVINCE_ID =
  "/province/districts?provinceId=";
var URL_API_WARDS_BY_DISTRICT_ID =
  "/district/wards?districtId=";
var URL_API_STREETS_BY_DISTRICT_ID =
  "/district/streets?districtId=";

/*** REGION 2 - Vùng gán / thực thi hàm xử lý sự kiện cho các elements */
$(document).ready(function () {
  onPageLoading();

  // Khi người dùng cuộn trang, thực hiện hàm scrollFunction
  $(window).scroll(function () {
    //Lấy vị trí hiện tại theo chiều dọc của thanh cuộn(gtri là số nguyên)
    let vScrollTop = $(this).scrollTop();

    // Kiểm tra nếu người dùng cuộn xuống
    if (vScrollTop > gLastScrollTop) {
      gScrolledDown = true;
    }

    // Nếu người dùng cuộn xuống rồi cuộn lên
    if (vScrollTop < gLastScrollTop && gScrolledDown) {
      if (vScrollTop > 50) {
        $("#btn-back-to-top").fadeIn();
      }
    } else {
      $("#btn-back-to-top").fadeOut();
    }

    // Ẩn nút nếu người dùng ở đầu trang
    if (vScrollTop === 0) {
      $("#btn-back-to-top").fadeOut();
    }

    gLastScrollTop = vScrollTop;
  });

  // Khi người dùng bấm vào nút, cuộn lên đầu trang
  $("#btn-back-to-top").click(function () {
    $("html, body").animate({ scrollTop: 0 }, "slow");
    return false; //ngăn chạn sự kiện mặc định và bong bóng lan truyền
  });

  // Lắng nghe sự kiện thay đổi (change event) trên phần tử input với id "input-photo"
  $("#input-photo").on("change", function (event) {
    // Lấy phần tử hiển thị tên file với id "file-name-display"
    const vFileNameDisplay = $("#file-name-display");

    // Lấy danh sách các file được chọn
    const files = event.target.files;

    // Kiểm tra xem có file nào được chọn không
    if (files.length > 0) {
      // Tạo một mảng chứa tên của tất cả các file được chọn
      const fileNames = $.map(files, function (file) {
        return file.name;
      }).join(", ");

      // Cập nhật nội dung văn bản của phần tử hiển thị tên file
      vFileNameDisplay.text(`Selected files: ${fileNames}`);
    } else {
      // Nếu không có file nào được chọn, xóa nội dung văn bản của phần tử hiển thị tên file
      vFileNameDisplay.text("");
    }
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
      callAPIGetProjectsByDistrictId(districtId);
    }
  });

  //Sự kiện click nút add
  $("#btn-add-property").click(onBtnAddProperty);

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
async function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    $("#login-icon").addClass("d-none")
    $("#user-icon").removeClass("d-none")
    $("#cart-icon").removeClass("d-none")
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
  } else {
    showToast(3, "You need to login to perform this feature");
    setTimeout(() => {
      window.location.href = gLOGIN_URL;
    }, 1000);
  }
  try {
    await callAPIGetAllProvinces();
    const provinceId = $("#select-province").val();
    if (provinceId !== "") {
      await callAPIGetDistrictsByProvinceId(provinceId);
      const districtId = $("#select-district").val();
      if (districtId !== "") {
        callAPIGetWardsByDistrictId(districtId);
        callAPIGetStreetsByDistrictId(districtId);
        callAPIGetProjectsByDistrictId(districtId);
      }
    }
  } catch (error) {
    try {
      const responseObject = JSON.parse(error.responseText);
      showToast(3, responseObject.message);
    } catch (e) {
      showToast(3, error.responseText || error.statusText);
    }
  }
}

// Hàm submit form
function onBtnAddProperty() {
  event.preventDefault();
  event.stopPropagation();
  //B0: Tạo object
  let vPropertyObj = {
    title: "",
    type: "",
    request: "",
    provinceId: null,
    districtId: null,
    wardId: null,
    streetId: null,
    projectId: null,
    address: "",
    price: 0,
    acreage: 0.0,
    direction: "",
    apartCode: "",
    bedroom: 0,
    furnitureType: "",
    priceRent: 0,
    description: "",
    photoFiles: null,
  };

  //B1: Thu thập
  collectDataSubmit(vPropertyObj);
  //B2: Validation(check đơn giản cho customer)
  let vCheck = validateDataSubmit();
  if (vCheck) {
    //B3: Call Api
    const accessToken = getAccessToken();
    callAPIAddProperty(accessToken, vPropertyObj);
  }
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//Call API
function callAPIGetAllProvinces() {
  return new Promise((resolve, reject) => {
    blockUI();
    $.ajax({
      url: URL_API_GET_PROVINCES,
      method: "GET",
      success: function (res) {
        loadDataToProvinceSelect(res);
        resolve(res);
      },
      error: function (error) {
        try {
          const responseObject = JSON.parse(error.responseText);
          showToast(3, responseObject.message);
        } catch (e) {
          showToast(3, error.responseText || error.statusText);
        }
        reject(error);
      },
      finally: unblockUI(),
    });
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
        try {
          const responseObject = JSON.parse(error.responseText);
          showToast(3, responseObject.message);
        } catch (e) {
          showToast(3, error.responseText || error.statusText);
        }
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
        try {
          const responseObject = JSON.parse(error.responseText);
          showToast(3, responseObject.message);
        } catch (e) {
          showToast(3, error.responseText || error.statusText);
        }
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
        try {
          const responseObject = JSON.parse(error.responseText);
          showToast(3, responseObject.message);
        } catch (e) {
          showToast(3, error.responseText || error.statusText);
        }
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
        try {
          const responseObject = JSON.parse(error.responseText);
          showToast(3, responseObject.message);
        } catch (e) {
          showToast(3, error.responseText || error.statusText);
        }
        reject(error);
      },
      finally: unblockUI(),
    });
  });
}

function callAPIAddProperty(paramAccessToken, paramPropertyObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  //tạo 1 formData để gửi file
  const formData = new FormData();

  // Duyệt qua tất cả các thuộc tính của đối tượng paramPropertyObj
  // Kiểm tra giá trị của từng thuộc tính, nếu giá trị không phải là falsy(optional)
  // Thêm cặp khóa-giá trị tương ứng vào formData
  // Bằng ko thì ko làm gì cả(null)
  for (const key in paramPropertyObj) {
    if (paramPropertyObj.hasOwnProperty(key)) {
      if (key === 'photoFiles' && paramPropertyObj[key].length > 0) {
        //do server nhận List<MultipartFile> nên cần append từng thuộc tính photoFiles riêng lẻ thay vì 1 array
        for (let i = 0; i < paramPropertyObj[key].length; i++) {
          formData.append(`${key}`, paramPropertyObj[key][i]);
        }
      } else {
        paramPropertyObj[key] ? formData.append(key, paramPropertyObj[key]) : null;
      }
    }
  }
  blockUI();
  $.ajax({
    type: "POST",
    headers: headers,
    url: gBASE_URL + "/real-estates",
    processData: false, // Đặt thành false để ngăn jQuery chuyển đổi dữ liệu thành chuỗi
    contentType: false, // Đặt thành false để ngăn jQuery đặt Content-Type(đã được thiết lập bởi FormData)
    data: formData,
    success: function (paramData) {
      showToast(1, "Add Property successfully");
      clearFormSubmit();
    },
    error: function (error) {
      try {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } catch (e) {
        showToast(3, error.responseText || error.statusText);
      }
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
      $("#user-name").html(paramData.username);
      $("#cart-icon .badge").html(paramData.realEstatesCount);
    },
    error: function(error) {
      try {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } catch (e) {
        showToast(3, error.responseText || error.statusText);
      }
      resetLogin();
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
      if (paramData) {
        $("#admin-icon").removeClass("d-none");
      }
    },
    error: function(error) {
      try {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } catch (e) {
        showToast(3, error.responseText || error.statusText);
      }
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
      try {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } catch (e) {
        showToast(3, error.responseText || error.statusText);
      }
    },
    finally: unblockUI(),
  });
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
    $("#select-district").prop("disabled", false); //bật select

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
    $("#select-ward").prop("disabled", false); //bật select

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
    $("#select-street").prop("disabled", false); //bật select

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
    $("#select-project").prop("disabled", false); //bật select

    for (let i = 0; i < projects.length; i++) {
      let option = $("<option/>");
      option.prop("value", projects[i].id);
      option.prop("text", projects[i].name);
      $("#select-project").append(option);
    }
  }
}

//Hàm thu thập dữ liệu
function collectDataSubmit(paramPropertyObj) {
  paramPropertyObj.title = $.trim($("#input-title").val());
  paramPropertyObj.type = $.trim($("#select-type").val());
  paramPropertyObj.request = $.trim($("#select-request").val());
  paramPropertyObj.provinceId = $.trim($("#select-province").val());
  paramPropertyObj.districtId = $.trim($("#select-district").val());
  paramPropertyObj.wardId = $.trim($("#select-ward").val()) || null;
  paramPropertyObj.streetId = $.trim($("#select-street").val()) || null;
  paramPropertyObj.projectId = $.trim($("#select-project").val()) || null;
  paramPropertyObj.address = $.trim($("#input-address").val());
  paramPropertyObj.direction = $.trim($("#select-direction").val());
  paramPropertyObj.apartCode = $.trim($("#input-apart-code").val());
  paramPropertyObj.furnitureType = $.trim($("#select-furniture-type").val());
  paramPropertyObj.description = $.trim($("#input-description").val());

  let vPrice = parseInt($.trim($("#input-price").val()));
  paramPropertyObj.price = isNaN(vPrice) ? null : vPrice;
  let vAcreage = parseFloat($.trim($("#input-acreage").val()));
  paramPropertyObj.acreage = isNaN(vAcreage) ? null : vAcreage;
  let vBedroom = parseInt($.trim($("#input-bedroom").val()));
  paramPropertyObj.bedroom = isNaN(vBedroom) ? null : vBedroom;
  let vPriceRent = parseInt($.trim($("#input-price-rent").val()));
  paramPropertyObj.priceRent = isNaN(vPriceRent) ? null : vPriceRent;

  paramPropertyObj.photoFiles = $("#input-photo")[0].files; //Lấy các file thuộc DOM đầu tiên của phần tử id "input-photo"
}

// Hàm validation
function validateDataSubmit() {
  // Lấy form có class .needs-validation
  const form = $(".needs-validation");

  // Kiểm tra tính hợp lệ của form bằng thuộc tính checkValidity() HTML5
  let isValid = form[0].checkValidity();

  // Thêm class 'was-validated' vào form để hiển thị các kiểu xác thực Bootstrap
  form.addClass("was-validated");

  // Lặp qua tất cả các thẻ select trong form
  form.find("select").each(function () {
    const select = $(this);
    if (
      select.prop("required") &&
      (select.val() === null || select.val() === "")
    ) {
      // Nếu thẻ select bắt buộc và không được chọn
      select.addClass("is-invalid");
      isValid = false;
    } else {
      select.removeClass("is-invalid");
    }
  });

  // Truy vấn và kiểm tra input[type="file"] trực tiếp dù nó ẩn
  const vInputPhoto = $("#input-photo");
  const vParent = vInputPhoto.closest('.property-attribute');
  const vValidFeedback = vParent.find('.valid-feedback');
  const vInvalidFeedback = vParent.find('.invalid-feedback');

  if (vInputPhoto.prop("required") && vInputPhoto.get(0).files.length === 0) {
    // Nếu thẻ input[type="file"] bắt buộc và không có tệp được chọn
    vInputPhoto.addClass("is-invalid");
    vInvalidFeedback.show();
    vValidFeedback.hide();
    isValid = false;
  } else {
    vInputPhoto.removeClass("is-invalid");
    vInvalidFeedback.hide();
    vValidFeedback.show();
  }

  return isValid;
}

//Hàm reset form
function clearFormSubmit() {
  // Lấy form có class .needs-validation
  const form = $(".needs-validation");
  // Reset form
  form[0].reset();
  // Xóa class 'was-validated'
  form.removeClass("was-validated");
  // Disable các select cần được disabled
  form.find("select.disable-on-reset").prop("disabled", true);
  // Xóa giá trị của input type=file(xóa hình thức)
  $("#file-name-display").html("");
  
  // Truy vấn input[type="file"] 
  const vInputPhoto = $("#input-photo");
  const vParent = vInputPhoto.closest('.property-attribute');
  const vValidFeedback = vParent.find('.valid-feedback');
  const vInvalidFeedback = vParent.find('.invalid-feedback');

  // Kiểm tra phần tử nào đang hiển thị và ẩn nó đi
  if (vValidFeedback.is(":visible")) {
      vValidFeedback.hide();
  } else if (vInvalidFeedback.is(":visible")) {
      vInvalidFeedback.hide();
  }
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
