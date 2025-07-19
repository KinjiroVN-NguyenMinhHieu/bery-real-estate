"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa

//URL page
let gLOGIN_URL = "login.html";
let gHOME_URL = "index.html";

//URL API
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

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    $("#cart-icon").removeClass("d-none");
    $("#user-icon").removeClass("d-none");
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
  } else {
    window.location.href = gLOGIN_URL;
  }
}

// Hàm submit
function onBtnSubmitForm() {
  event.preventDefault();
  event.stopPropagation();
  //B0: Tạo object
  let vPasswordObj = {
    oldPassword: "",
    newPassword: "",
  };
  //B1: Thu thập
  collectDataSubmit(vPasswordObj);
  //B2: Validation
  let vCheck = validateDataSubmit();
  if (vCheck) {
    //B3: Call Api
    const accessToken = getAccessToken();
    callAPIChangePassword(accessToken, vPasswordObj);
  }
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
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
      }
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
        try {
          const responseObject = JSON.parse(error.responseText);
          showToast(3, responseObject.message);
        } catch (e) {
          showToast(3, error.responseText || error.statusText);
        }
      }
  });
}

//api change password
function callAPIChangePassword(paramAccessToken, pPasswordObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
    type: "PUT",
    headers: headers,
    url: gAUTH_URL + "/password",
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(pPasswordObj),
    success: function (paramData) {
      showToast(1, "Change password successfully");
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
  });
}

//Hàm thu thập dữ liệu
function collectDataSubmit(pPasswordObj) {
  pPasswordObj.oldPassword = $.trim($("#input-old-password").val());
  pPasswordObj.newPassword = $.trim($("#input-new-password").val());
}

//hàm validate form
function validateDataSubmit() {
  const form = $(".needs-validation");
  
  // Kiểm tra password và confirm password
  const oldPassword = $("#input-old-password").val();
  const newPassword = $("#input-new-password").val();
  const confirmNewPassword = $("#input-confirm-new-password").val();
  const minLength = 6;

  // Kiểm tra độ dài tối thiểu và độ khớp của mật khẩu
  if (newPassword.length < minLength) {
    $("#input-new-password")[0].setCustomValidity("error");
    $("#input-new-password").closest('.col-12').find('.invalid-feedback').eq(0).show();
    $("#input-new-password").closest('.col-12').find('.invalid-feedback').eq(1).hide();
  } else if (newPassword === oldPassword) {
    $("#input-new-password")[0].setCustomValidity("error");
    $("#input-new-password").closest('.col-12').find('.invalid-feedback').eq(0).hide();
    $("#input-new-password").closest('.col-12').find('.invalid-feedback').eq(1).show();
  } else {
    $("#input-new-password")[0].setCustomValidity("");
    $("#input-new-password").closest('.col-12').find('.invalid-feedback').hide();
  }

  if (newPassword !== confirmNewPassword) {
    $("#input-confirm-new-password")[0].setCustomValidity("error");
    $("#input-confirm-new-password").closest('.col-12').find('.invalid-feedback').show();
  } else {
    $("#input-confirm-new-password")[0].setCustomValidity("");
    $("#input-confirm-new-password").closest('.col-12').find('.invalid-feedback').hide();
  }

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
