"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa

//URL page
let gHOME_URL = "index.html";
let gPROPERTIES_URL = "properties.html";
let gPROPERTIES_DETAILS_URL = "properties-details.html";

//URL API
let gBASE_URL = "/real-estates";
let gAUTH_URL = "/auth";
//Biến lưu trữ thông tin
let gNumberFeaturedOfElements = 8; //số bản ghi featured 1 page
let gNumberLuxuryOfElements = 4; //số bản ghi luxury 1 page
let gTotalElements; //tổng số bản ghi
let gTotalPages; //tổng số page

/*** REGION 2 - Vùng gán / thực thi hàm xử lý sự kiện cho các elements */
$(document).ready(function () {
  onPageLoading();

  // Sự kiện gõ ô input
  $("#input-search").on("keypress keydown", function (event) {
    // Kiểm tra nếu phím Enter được nhấn (keyCode 13)
    if (event.keyCode === 13) {
      event.preventDefault(); // Ngăn chặn hành động mặc định của phím Enter (ví dụ: gửi form)

      // Thực hiện tìm kiếm khi phím Enter được nhấn
      onBtnSearchClick();
    }
  });

  $("#btn-search").click(onBtnSearchClick);

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
  
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    $("#login-icon").addClass("d-none")
    $("#user-icon").removeClass("d-none")
    $("#cart-icon").removeClass("d-none")
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
  }
  callAPICountAllRealEstByCity();
  // Thực hiện xử lý hiển thị của trang đầu tiên
  // Các trang tiếp theo gán onclick trong nút phân trang và gọi tương tự trang đầu tiên
  createFeaturedPage(1);
  createLuxuryPage(1);
}

// Hàm thực hiện tìm kiếm
function onBtnSearchClick() {
  let vKeyword = $.trim($("#input-search").val());
  if (vKeyword) {
    let vKeywordParam = new URLSearchParams({ keyword: vKeyword });
    window.location.href = gPROPERTIES_URL + "?" + vKeywordParam.toString();
  }
}

// Hàm get detail
function getDetailsClick(paramCard) {
  let vId = $(paramCard).find(".card-id").text();
  let vIdParam = new URLSearchParams({ id: vId });
  window.location.href = gPROPERTIES_DETAILS_URL + "?" + vIdParam.toString();
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//hàm tạo featured pagination page
function createFeaturedPage(paramPagenum) {
  $("#featured-properties-section .spinner-pagination").removeClass("d-none");
  callAPIGetAllRealEstatesPagination(gNumberFeaturedOfElements, paramPagenum);
}

//hàm tạo luxury pagination page
function createLuxuryPage(paramPagenum) {
  $("#luxury-properties-section .spinner-pagination").removeClass("d-none");
  callAPIGetAllLuxuryRealEstatesPagination(
    gNumberLuxuryOfElements,
    paramPagenum
  );
}

//api get all featured
function callAPIGetAllRealEstatesPagination(
  paramNumberOfElements,
  paramPagenum
) {
  const vQueryParams = new URLSearchParams({
    page: paramPagenum - 1,
    size: paramNumberOfElements,
  });

  $.ajax({
    type: "get",
    url: gBASE_URL + "/pagination?" + vQueryParams.toString(),
    dataType: "json",
    success: function (paramData) {
      let vContainer = $("#featured-properties-section");
      handleDataPagination(vContainer, paramData, paramPagenum);
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

//api get all luxury
function callAPIGetAllLuxuryRealEstatesPagination(
  paramNumberOfElements,
  paramPagenum
) {
  const vQueryParams = new URLSearchParams({
    page: paramPagenum - 1,
    size: paramNumberOfElements,
  });

  $.ajax({
    type: "get",
    url: gBASE_URL + "/pagination/luxury?" + vQueryParams.toString(),
    dataType: "json",
    success: function (paramData) {
      let vContainer = $("#luxury-properties-section");
      handleDataPagination(vContainer, paramData, paramPagenum);
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

//api đếm số lượng properties tương ứng 4 thành phố lớn nhất
function callAPICountAllRealEstByCity() {
  $.ajax({
    type: "get",
    url: gBASE_URL + "/count/province",
    dataType: "json",
    success: function (paramData) {
      handleDataCountPropertiesByProvince(paramData);
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

//Hàm xử lý data phân trang
function handleDataPagination(paramContainer, paramData, paramPagenum) {
  gTotalElements = paramData.totalElements; //lấy tổng số bản ghi
  gTotalPages = paramData.totalPages; //lấy tổng số page

  let vContentContainer = paramContainer.find(".content-container");
  displayDataGetAllRealEstatesPagination(vContentContainer, paramData); //Gọi hàm xử lý hiển thị lấy danh sách phân trang

  let vPaginationContainer = paramContainer.find(".pagination-container");
  createPagination(vPaginationContainer, paramPagenum); // Gọi hàm tạo thanh phân trang

  paramContainer.find(".spinner-pagination").addClass("d-none"); //Ẩn spinner
}

//Hàm xử lý data đếm properties
function handleDataCountPropertiesByProvince(paramData) {
  let vCitiesContainer = $("#explore-cities-section");

  for (const [code, count] of paramData) {
    //tìm div con có data-code phù hợp
    const vCityContainer = vCitiesContainer.find(`[data-code = "${code}"]`);
    //nếu tìm thấy(length > 0)
    if (vCityContainer.length) {
      vCityContainer.find(".properties-count").html(count);
    }
  }
}

//Hàm xử lý hiển thị lấy danh sách records có phân trang
function displayDataGetAllRealEstatesPagination(
  paramContentContainer,
  paramData
) {
  paramContentContainer.empty();
  //Dùng Object.values đổi giá trị order trả về thành 1 mảng để dùng forEach
  let vRecordsArr = Object.values(paramData.content);
  if (vRecordsArr.length > 0) {
    vRecordsArr.forEach((paramRecords) => {
      paramContentContainer.append(`
              <div class="col-12 col-md-6 col-lg-3 mb-5 d-flex justify-content-center">
                  <div class="estate-card card" style="width: 20rem;" onclick="getDetailsClick(this)">
                      <div class="estate-card-top mx-auto">
                        <img src="${paramRecords.photosUrl[0]}" class="card-img-top img-fluid mx-auto" alt="Estate Image">
                        <div><small class="card-request fst-italic">FOR ${paramRecords.request}</small></div>
                      </div>
                      <div class="card-body">
                        <p class="card-id d-none">${paramRecords.id}</p>
                        <p class="card-title">${paramRecords.title}</p>
                        <p class="card-address">${paramRecords.address}</p>
                        <p class="card-created">Added: ${paramRecords.createdAt}</p>
                        <p class="card-description">${paramRecords.description}</p>
                        <div class="d-flex justify-content-between">
                            <div class="col-4 d-flex justify-content-start"><small class="card-acreage"><i class="fa-solid fa-vector-square"></i> ${paramRecords.acreage}m2</small></div>
                            <div class="col-4 d-flex justify-content-center"><small class="card-acreage"><i class="fa-solid fa-couch"></i> ${paramRecords.furnitureType}</small></div>
                            <div class="col-4 d-flex justify-content-end"><small class="card-bedroom"><i class="fa-solid fa-bed"></i> ${paramRecords.bedroom}</small></div>
                        </div>
                      </div>
                      <div class="card-footer d-flex justify-content-between">
                        <div class="col-6 d-flex justify-content-start"><small class="card-price">Price: ${paramRecords.price} mil. đ</small></div>
                        <div class="col-6 d-flex justify-content-end"><small class="card-province"><i class="fa-solid fa-map-location-dot"></i> ${paramRecords.provinceName}</small></div>
                      </div>
                  </div>
              </div>
          `);
    });
  }
}

// Hàm tạo thanh phân trang
function createPagination(paramPaginationContainer, paramPagenum) {
  paramPaginationContainer.empty();
  let createPage; //khai báo hàm chung dùng để gán(khi gán sẽ dùng thuộc tính .name để tham chiếu tới tên hàm đc gán cho functionCallAPI)
  //Kiểm tra container và gán hàm
  if (
    paramPaginationContainer.is(
      "#featured-properties-section .pagination-container"
    )
  ) {
    createPage = createFeaturedPage;
  } else {
    createPage = createLuxuryPage;
  }
  // Nếu tran hiện tại là trang 1 thì nút Prev sẽ bị disable
  if (paramPagenum == 1) {
    paramPaginationContainer.append(
      "<li class='page-item disabled previous mx-1'><a href='javascript:void(0)' class='page-link rounded-circle'><i class='fas fa-chevron-circle-left'></i></a></li>"
    );
  } else {
    paramPaginationContainer.append(
      "<li class='page-item mx-1' onclick='" +
        createPage.name +
        "(" +
        (paramPagenum - 1) +
        ")'><a href='javascript:void(0)' class='page-link rounded-circle'><i class='fas fa-chevron-circle-left'></i></a></li>"
    );
  }

  // Nếu tran hiện tại là trang cuối cùng thì nút Next sẽ bị disable
  if (paramPagenum == gTotalPages) {
    paramPaginationContainer.append(
      "<li class='page-item disabled mx-1'><a href='javascript:void(0)' class='page-link rounded-circle'><i class='fas fa-chevron-circle-right'></i></a></li>"
    );
  } else {
    paramPaginationContainer.append(
      "<li class='page-item next mx-1' onclick='" +
        createPage.name +
        "(" +
        (paramPagenum + 1) +
        ")'><a href='javascript:void(0)' class='page-link rounded-circle'><i class='fas fa-chevron-circle-right'></i></a></li>"
    );
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
