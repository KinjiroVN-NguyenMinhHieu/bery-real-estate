"use strict";
/*** REGION 1 - Global letiables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
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

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });

  // change profile
  $("#info-section").on("click", "#btn-change-profile", function () {
    $("#info-section").addClass("d-none");
    $("#form-section").removeClass("d-none");
  });

  //ấn confirm update
  $("#btn-submit-form").click(onBtnSubmitForm);

  //cancel update
  $("#btn-cancel-form").click(() => {
    event.preventDefault();
    event.stopPropagation();
    $("#info-section").removeClass("d-none");
    $("#form-section").addClass("d-none");
  });

  // Lắng nghe sự kiện change của input có id là 'input-photo'
  $('#input-photo').change(function(event) {
    // Lấy tệp tin đầu tiên từ sự kiện change
    const file = event.target.files[0];
    // Kiểm tra xem tệp tin đã được chọn hay chưa
    if (file) {
      // Tạo một đối tượng FileReader để đọc nội dung của tệp tin
      const reader = new FileReader();
      // Đăng ký hàm xử lý sự kiện khi tệp tin được đọc thành công
      reader.onload = function(e) {
          // Cập nhật thuộc tính src của phần tử có id là 'user-avatar' với dữ liệu URL của tệp tin
          $('#user-avatar').attr('src', e.target.result);
      };
      // Bắt đầu quá trình đọc tệp tin dưới dạng URL dữ liệu (base64)
      reader.readAsDataURL(file);
    }
  });
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    $("#user-icon").removeClass("d-none");
    $("#cart-icon").removeClass("d-none");
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
    callAPIGetProfileUser(accessToken);
  } else {
    window.location.href = gLOGIN_URL;
  }
}
// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

// Hàm submit
function onBtnSubmitForm() {
  event.preventDefault();
  event.stopPropagation();
  //B0: Tạo object
  let vProfileObj = {
    lastName: "",
    firstName: "",
    userName: "",
    email: "",
    birthDate: null,
    address: "",
    city: "",
    country: "",
    homePhone: "",
    note: "",
    photoFile: null
  };

  //B1: Thu thập
  collectDataSubmit(vProfileObj);
  //B2: Validation
  let vCheck = validateDataSubmit();
  if (vCheck) {
    //B3: Call Api
    const accessToken = getAccessToken();
    callAPIChangeProfile(accessToken, vProfileObj);
  }
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
        //check danh tính ng dùng
        let username = $(".employee-container .card-info-title").text();
        if(username === paramData.username) {
          $("#modify-property").removeClass("d-none")
        }
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

//api get profile ng dùng
function callAPIGetProfileUser(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
      url: gAUTH_URL + "/profile",
      method: "GET",
      headers: headers,
      success: function(paramData) {
        handleDataProfile(paramData);
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

function callAPIChangeProfile(paramAccessToken, paramProfileObj) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  //tạo 1 formData để gửi file
  const formData = new FormData();

  // Duyệt qua tất cả các thuộc tính của đối tượng paramProfileObj
  // Kiểm tra giá trị của từng thuộc tính, nếu giá trị không phải là falsy(optional)
  // Thêm cặp khóa-giá trị tương ứng vào formData
  // Bằng ko thì ko làm gì cả(null)
  for (const key in paramProfileObj) {
    if (paramProfileObj.hasOwnProperty(key)) {
      paramProfileObj[key] ? formData.append(key, paramProfileObj[key]) : null;
    }
  }

  $.ajax({
    type: "PUT",
    headers: headers,
    url: gAUTH_URL + "/profile",
    processData: false, // Đặt thành false để ngăn jQuery chuyển đổi dữ liệu thành chuỗi
    contentType: false, // Đặt thành false để ngăn jQuery đặt Content-Type(đã được thiết lập bởi FormData)
    data: formData,
    success: function (paramData) {
      showToast(1, "Update Profile successfully");
      clearFormSubmit();
      callAPIGetProfileUser(paramAccessToken);
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

// Hàm handle data profile
function handleDataProfile(paramData) {
  //Hiển thị và gán info-section
  $("#info-section").empty().append(`
    <div class="col-12 col-lg-6 g-3">
      <div class="col-12 d-flex align-items-center justify-content-center">
        <img src="${paramData.photo ? paramData.photo : "./assets/images/user/user_avatar.png"}" class="img-thumbnail img-fluid" style="width: 10rem; height: 10rem;" alt="user-avatar">
      </div>
      <div id="btn-change-profile" class="btn">
        <i class="fa-solid fa-user-pen"></i> &nbsp;
        Change your profile
      </div>
      <div class="col-12 row">
        <div class="col-4">Username</div>
        <div class="col-8 fw-bold">${paramData.userName ? paramData.userName : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">First Name</div>
        <div class="col-8 fw-bold">${paramData.firstName ? paramData.firstName : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Last Name</div>
        <div class="col-8 fw-bold">${paramData.lastName ? paramData.lastName : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Email</div>
        <div class="col-8 fw-bold">${paramData.email ? paramData.email : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Home Phone</div>
        <div class="col-8 fw-bold">${paramData.homePhone ? paramData.homePhone : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Birth Date</div>
        <div class="col-8 fw-bold">${paramData.birthDate ? paramData.birthDate : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Address</div>
        <div class="col-8 fw-bold">${paramData.address ? paramData.address : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">City</div>
        <div class="col-8 fw-bold">${paramData.city ? paramData.city : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Country</div>
        <div class="col-8 fw-bold">${paramData.country ? paramData.country : ""}</div>
      </div>
      <div class="col-12 row">
        <div class="col-4">Note</div>
        <div class="col-8 fw-bold">${paramData.note ? paramData.note : ""}</div>
      </div>
    </div>
  `).removeClass("d-none");

  //Gán giá trị cho các input và ẩn form-section
  $("#form-section").addClass("d-none");
  $("#user-avatar").attr("src", paramData.photo ? paramData.photo : "./assets/images/user/user_avatar.png");
  $("#input-username").val(paramData.userName);
  $("#input-firstname").val(paramData.firstName);
  $("#input-lastname").val(paramData.lastName);
  $("#input-email").val(paramData.email);
  $("#input-homephone").val(paramData.homePhone);
  $("#input-birthdate").val(convertDateFormat(paramData.birthDate));//đổi format để trình duyệt hiểu đc
  $("#input-address").val(paramData.address);
  $("#input-city").val(paramData.city);
  $("#input-country").val(paramData.country);
  $("#input-note").val(paramData.note);

  //kiểm tra sự kiện lỗi khi trình duyệt load ảnh để gán lại
  $("#info-section img, #user-avatar").on('error', function() {
    $(this).attr("src", paramData.photo);
  });    
}

//Hàm thu thập dữ liệu
function collectDataSubmit(paramProfileObj) {
  paramProfileObj.userName = $.trim($("#input-username").val());
  paramProfileObj.firstName = $.trim($("#input-firstname").val());
  paramProfileObj.lastName = $.trim($("#input-lastname").val());
  paramProfileObj.email = $.trim($("#input-email").val());
  paramProfileObj.homePhone = $.trim($("#input-homephone").val());
  paramProfileObj.birthDate = new Date($("#input-birthdate").val());//đổi sang Date để khớp với Date ở server
  paramProfileObj.address = $.trim($("#input-address").val());
  paramProfileObj.city = $.trim($("#input-city").val());
  paramProfileObj.country = $.trim($("#input-country").val());
  paramProfileObj.note = $.trim($("#input-note").val());
  paramProfileObj.photoFile = $("#input-photo")[0].files[0];
}

// Hàm validation
function validateDataSubmit() {
  // Lấy form có class .needs-validation
  const form = $(".needs-validation");

  // Kiểm tra tính hợp lệ của form bằng thuộc tính checkValidity() HTML5
  let isValid = form[0].checkValidity();

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

//chuyển ngày dd-MM-yyyy thành yyyy-MM-dd(để trình duyệt hiểu)
function convertDateFormat(paramDateString) {
  if (paramDateString) {
    const dateParts = paramDateString.split("-");
    return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
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
