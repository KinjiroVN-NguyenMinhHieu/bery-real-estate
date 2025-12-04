"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa

let gHOME_URL = "index.html";

//URL API
let gBASE_URL = window.location.origin;
let gAUTH_URL = "/auth";

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

  //Sự kiện click nút submit
  $("#btn-submit-contact").click(onBtnSubmitContact);

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
// Hàm tải trang
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    $("#login-icon").addClass("d-none")
    $("#user-icon").removeClass("d-none")
    $("#cart-icon").removeClass("d-none")
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
  }
}


// Hàm submit contact
function onBtnSubmitContact() {
  event.preventDefault();
  event.stopPropagation();
  //B0: Tạo object
  let vCustomerObj = {
    contactName: "",
    contactTitle: "",
    address: "",
    mobile: "",
    email: "",
    note: "",
  };
  //B1: Thu thập
  collectDataSubmit(vCustomerObj);
  //B2: Validation(check đơn giản cho customer)
  let vCheck = validateDataSubmit();
  if (vCheck) {
    //B3: Call Api
    callAPISubmitCustomer(vCustomerObj);
  }
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//api
function callAPISubmitCustomer(paramCustomerObj) {
  blockUI();
  $.ajax({
    type: "POST",
    url: gBASE_URL + "/customers",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(paramCustomerObj),
    success: function (paramData) {
      showToast(1, "Create contact information successfully");
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

//Hàm thu thập dữ liệu
function collectDataSubmit(paramCustomerObj) {
  paramCustomerObj.contactName =
    $.trim($("#input-first-name").val()) + $.trim($("#input-last-name").val());
  paramCustomerObj.contactTitle = $.trim($("#input-title").val());
  paramCustomerObj.address = $.trim($("#input-address").val());
  paramCustomerObj.mobile = $.trim($("#input-mobile").val());
  paramCustomerObj.email = $.trim($("#input-email").val());
  paramCustomerObj.note = $.trim($("#input-note").val());
}

// Hàm validation
function validateDataSubmit() {
  // Lấy form có class .needs-validation(trả về 1 array)
  const form = $(".needs-validation");

  // Kiểm tra tính hợp lệ của form bằng thuộc tính checkValidity() HTML5
  const isValid = form[0].checkValidity();

  // Thêm class 'was-validated' vào form để hiển thị các kiểu xác thực Bootstrap
  form.addClass("was-validated");

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
