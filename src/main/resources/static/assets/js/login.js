"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa
let gRememberMe = false; //biến lưu trạng thái remember me

//URL API
let gBASE_URL = "/auth";

let gHOME_URL = "index.html"

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

  // Bắt sự kiện click vào thẻ cha của input password
  $(".password").parent().click(function () {
    // Tìm input password trong thẻ cha đang được click
    const passwordInput = $(this).find(".password");
    // Kích hoạt focus cho input
    passwordInput.focus();
  });

  // Bắt sự kiện click cho biểu tượng con mắt
  $(".togglePassword").click(function () {
    //chọn class password cùng cấp(tức chỉ 1 ô password)
    const passwordInput = $(this).siblings(".password");
    const fieldType = passwordInput.attr("type");
    // Đảo ngược kiểu của input để hiển thị hoặc ẩn mật khẩu
    passwordInput.attr("type", fieldType === "password" ? "text" : "password");
    // Thay đổi biểu tượng con mắt để phản ánh trạng thái hiển thị mật khẩu
    $(this).toggleClass("fa-eye fa-eye-slash");
  });

  // Bắt sự kiện focus cho input
  $(".password").focus(function () {
    $(this).siblings(".togglePassword").css("color", "#666");
  });

  // Bắt sự kiện blur cho input
  $(".password").blur(function () {
    $(this).siblings(".togglePassword").css("color", "transparent");
  });

  //Sự kiện click nút submit
  $("#btn-submit-form").click(onBtnSubmitForm);

  //Sự kiện forgotpassword
  $("#btn-send-link").click(onBtnSendLink);

  // Khi modal được ẩn, gọi hàm clearFormSubmitForgotPassword
  $('#forgot-password-modal').on('hidden.bs.modal', function () {
    clearFormSubmitForgotPassword();
  });

});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = Cookies.get('accessToken');
  if (accessToken) {
    setTimeout(() => {
      window.location.href = gHOME_URL;
    }, 1000);
  }
}

// Hàm submit login
function onBtnSubmitForm() {
  event.preventDefault();
  event.stopPropagation();
  //B0: Tạo object
  let pAccountObj = {
    username: "",
    password: "",
  };
  //B1: Thu thập
  collectDataSubmit(pAccountObj);
  //B2: Validation
  checkRememberMe();
  let vCheck = validateDataSubmit();
  if (vCheck) {
    //B3: Call Api
    callAPILoginAccount(pAccountObj);
  }
}

// Hàm click send link forgot password
function onBtnSendLink() {
  event.preventDefault();
  event.stopPropagation();
  //B1: Thu thập
  let vEmail = $.trim($("#input-email").val());
  //B2: Validation(check đơn giản cho customer)
  let vCheck = validateDataSubmitForgotPassword();
  if (vCheck) {
    //B3: Call Api
    callAPISubmitEmailForgotPassword(vEmail);
  }
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//api
function callAPILoginAccount(pAccountObj) {
  blockUI();
  $.ajax({
    type: "POST",
    url: gBASE_URL + "/login",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(pAccountObj),
    success: function (paramData) {
      handleLoginSuccess(paramData);
    },
    error: function (error) {
      if (error.responseText) {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } else {
        showToast(3, error.statusText);
      }
    },
    finally: unblockUI(),
  });
}

function callAPISubmitEmailForgotPassword(paramEmail) {
  $("#forgot-password-spinner").removeClass("d-none");
  blockUI();
  $.ajax({
    type: "POST",
    url: gBASE_URL + "/forgot-password?email=" + paramEmail,
    success: function (paramData) {
      $("#forgot-password-spinner").addClass("d-none");
      showToast(1, "Send email reset password successfully\nPlease check your email to reset password");
      $("#forgot-password-modal").modal('hide');
    },
    error: function (error) {
      $("#forgot-password-spinner").addClass("d-none");
      if (error.responseText) {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } else {
        showToast(3, error.statusText);
      }
    },
    finally: unblockUI(),
  });
}

//handle
function handleLoginSuccess(paramData) {
  if (gRememberMe) {
    Cookies.set('accessToken', paramData.accessToken, { expires: 7 });
  } else {
    sessionStorage.setItem('accessToken', paramData.accessToken);
  }
  showToast(1, "Login Account successfully");
  setTimeout(() => {
    window.location.href = gHOME_URL;
  }, 1000);
}

//Hàm thu thập dữ liệu
function collectDataSubmit(pAccountObj) {
  pAccountObj.username = $.trim($("#input-username").val());
  pAccountObj.password = $.trim($("#input-password").val());
}

function validateDataSubmit() {
  const form = $(".needs-validation");

  // Kiểm tra tính hợp lệ của form bằng thuộc tính checkValidity() HTML5
  const isValid = form[0].checkValidity();

  // Thêm class 'was-validated' vào form để hiển thị các kiểu xác thực Bootstrap
  form.addClass("was-validated");
  return isValid;
}

// Hàm check remember me
function checkRememberMe() {
  const vCheck = $("#input-checkbox").prop("checked");
  if (vCheck) {
    gRememberMe = true;
  } else {
    gRememberMe = false;
  }
}

// Hàm validation
function validateDataSubmitForgotPassword() {
  // Lấy form có class .needs-validation(trả về 1 array)
  const form = $("#forgot-password-modal .needs-validation");

  // Kiểm tra tính hợp lệ của form bằng thuộc tính checkValidity() HTML5
  const isValid = form[0].checkValidity();

  // Thêm class 'was-validated' vào form để hiển thị các kiểu xác thực Bootstrap
  form.addClass("was-validated");

  return isValid;
}

//Hàm reset form
function clearFormSubmitForgotPassword() {
  // Lấy form có class .needs-validation
  const form = $("#forgot-password-modal .needs-validation");

  // Reset form
  form[0].reset();

  // Xóa class 'was-validated'
  form.removeClass("was-validated");
}

