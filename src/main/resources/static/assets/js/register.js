"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa

//URL API
let gBASE_URL = "/auth";

let gLOGIN_URL = "login.html"

/*** REGION 2 - Vùng gán / thực thi hàm xử lý sự kiện cho các elements */
$(document).ready(function () {
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
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
// Hàm submit register
function onBtnSubmitForm() {
  event.preventDefault();
  event.stopPropagation();
  //B0: Tạo object
  let vNewAccountObj = {
    username: "",
    email: "",
    password: "",
  };
  //B1: Thu thập
  collectDataSubmit(vNewAccountObj);
  //B2: Validation
  let vCheck = validateDataSubmit();
  if (vCheck) {
    //B3: Call Api
    callAPIRegisterNewAccount(vNewAccountObj);
  }
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//api
function callAPIRegisterNewAccount(pNewAccountObj) {
  $.ajax({
    type: "POST",
    url: gBASE_URL + "/register",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(pNewAccountObj),
    success: function (paramData) {
      showToast(1, "Register new Account successfully");
      clearFormSubmit();
      setTimeout(() => {
        window.location.href = gLOGIN_URL;
      }, 1000);
    },
    error: function (error) {
      if (error.responseText) {
        const responseObject = JSON.parse(error.responseText);
        showToast(3, responseObject.message);
      } else {
        showToast(3, error.statusText);
      }
    },
  });
}

//Hàm thu thập dữ liệu
function collectDataSubmit(pNewAccountObj) {
  pNewAccountObj.username = $.trim($("#input-username").val());
  pNewAccountObj.email = $.trim($("#input-email").val());
  pNewAccountObj.password = $.trim($("#input-password").val());
}

function validateDataSubmit() {
  const form = $(".needs-validation");

  // Kiểm tra tính hợp lệ của form bằng thuộc tính checkValidity() HTML5
  const isValid = form[0].checkValidity();

  // Kiểm tra checkbox
  const checkbox = $("#input-checkbox");
  if (!checkbox.prop("checked")) {
    checkbox.closest('.col-12').find('.invalid-feedback').show();
  } else {
    checkbox.closest('.col-12').find('.invalid-feedback').hide();
  }

  // Kiểm tra password và confirm password
  const password = $("#input-password").val();
  const confirmPassword = $("#input-confirm-password").val();
  const minLength = 6;

  // Kiểm tra độ dài tối thiểu và độ khớp của mật khẩu
  if (password.length < minLength) {
    $("#input-password")[0].setCustomValidity("error");
    $("#input-password").closest('.col-12').find('.invalid-feedback').show();
    if (password !== confirmPassword) {
      $("#input-confirm-password")[0].setCustomValidity("error");
      $("#input-confirm-password").closest('.col-12').find('.invalid-feedback').show();
    } else {
      $("#input-confirm-password")[0].setCustomValidity("error");
      $("#input-confirm-password").closest('.col-12').find('.invalid-feedback').hide();
    }
  } else {
    if (password !== confirmPassword) {
      $("#input-confirm-password")[0].setCustomValidity("error");
      $("#input-confirm-password").closest('.col-12').find('.invalid-feedback').show();
    } else {
      $("#input-password")[0].setCustomValidity("");
      $("#input-confirm-password")[0].setCustomValidity("");
      $("#input-password").closest('.col-12').find('.invalid-feedback').hide();
      $("#input-confirm-password").closest('.col-12').find('.invalid-feedback').hide();
    }
  }

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
