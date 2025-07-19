"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa
const gLimitRow = 10;//biến limit số lượng bản ghi trả về tăng thêm mỗi lần

//URL page
let gLOGIN_URL = "login.html";
let gHOME_URL = "index.html";
let gPROPERTIES_DETAILS_URL = "properties-details.html";

//URL API
let gBASE_URL = "/real-estates";
let gAUTH_URL = "/auth";

// Khai báo các hằng số chứa thông tin mặc định về các cột
const gDATA_COLUMN = ["title", "type", "request", "price", "acreage", "status", "createdAt", "action"];
const gTITLE_COL = 0;
const gTYPE_COL = 1;
const gREQUEST_COL = 2;
const gPRICE_COL = 3;
const gACREAGE_COL = 4;
const gSTATUS_COL = 5;
const gCREATED_AT_COL = 6;
const gACTION_COL = 7;

// Cấu hình bảng khởi tạo DataTable
var gTable = $("#properties-table").DataTable({
  searching: false,
  ordering: false,
  paging: false,
  info: false,
  columns: [
    { data: gDATA_COLUMN[gTITLE_COL], width: "20%" },
    { data: gDATA_COLUMN[gTYPE_COL], width: "10%" },
    { data: gDATA_COLUMN[gREQUEST_COL], width: "10%" },
    { data: gDATA_COLUMN[gPRICE_COL], width: "10%" },
    { data: gDATA_COLUMN[gACREAGE_COL], width: "10%" },
    { data: gDATA_COLUMN[gSTATUS_COL], width: "10%" },
    { data: gDATA_COLUMN[gCREATED_AT_COL], width: "10%" },
    { data: gDATA_COLUMN[gACTION_COL], width: "5%" },
  ],
  columnDefs: [
    {
      target: gPRICE_COL,
      className: "text-center",
    },
    {
      target: gACREAGE_COL,
      className: "text-center",
    },
    {
      target: gACTION_COL,
      className: "text-center",
      defaultContent: `
        <div class="d-flex justify-content-center">
        <i class="fa-solid fa-circle-info detail-icon btn text-primary mx-auto"></i>
        </div>
      `,
    },
  ],
});

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

  //click detail icon
  $("#properties-table").on("click", ".detail-icon", function() {
    onDetailIconClick(this);
  });

  //click read-more btn
  $("#read-more").click(() => callAPIGetPublishProperties(getAccessToken(), countRowTable() + gLimitRow));
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    $("#cart-icon").removeClass("d-none");
    $("#user-icon").removeClass("d-none");
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
    callAPIGetUnpublishProperties(accessToken, countRowTable() + gLimitRow);
  } else {
    window.location.href = gLOGIN_URL;
  }
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

//hàm click detail icon
function onDetailIconClick(paramIcon) {
  let vId = collectIdRowClick(paramIcon);
  window.location.href = gPROPERTIES_DETAILS_URL + "?id=" + vId;
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

//api lấy dữ liệu giỏ hàng
function callAPIGetUnpublishProperties(paramAccessToken, paramSize) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };

  $.ajax({
      url: gBASE_URL + "/unpublish/limit?size=" + paramSize,
      method: "GET",
      headers: headers,
      success: function(paramData) {
        loadDataToTable(paramData.content);
        const vCountRow = countRowTable();
        if (vCountRow > 0 && vCountRow < paramData.totalElements) {
          $("#read-more").removeClass("d-none");
        } else {
          $("#read-more").addClass("d-none");
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

//handle function
function loadDataToTable(paramResponse) {
  gTable.clear();
  gTable.rows.add(paramResponse);
  gTable.draw();
}

// Hàm thu thập thông tin về ID của row mà icon đc click
function collectIdRowClick(paramIcon) {
  let vRowClick = $(paramIcon).closest("tr");
  let vRowData = gTable.row(vRowClick).data();
  return vRowData.id; 
}

// Hàm đếm số row trong table
function countRowTable() {
  let count = gTable.rows().count();
  return count;
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
