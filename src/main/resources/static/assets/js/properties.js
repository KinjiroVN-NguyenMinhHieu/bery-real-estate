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
var URL_API_GET_PROVINCES = "/provinces";
let gAUTH_URL = "/auth";
//Biến lưu trữ thông tin
let gNumberOfElements = 6; //số bản ghi filter 1 page
let gTotalElements; //tổng số bản ghi
let gTotalPages; //tổng số page

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

  //sự kiện bấm nút filter
  $("#btn-filter").click(() => {
    createPage(1);
  });

  //sự kiện bấm nút clear filter
  $("#btn-clear-filter").click(onBtnClearFilterClick);

  // Sự kiện gõ ô input
  $("#input-search").on("keypress keydown", function (event) {
    // Kiểm tra nếu phím Enter được nhấn (keyCode 13)
    if (event.keyCode === 13) {
      event.preventDefault(); // Ngăn chặn hành động mặc định của phím Enter (ví dụ: gửi form)

      // Thực hiện tìm kiếm khi phím Enter được nhấn
      onBtnSearchClick();
    }
  });

  //sự kiện bấm nút search
  $("#btn-search").click(onBtnSearchClick);

  //Sự kiện nhấn tags
  $('.tags-container .btn').click(function() {
    onClickTags(this);
  })

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
  }
  await callAPIGetAllProvinces();
  //lấy dữ liệu trên query và đổ form(lưu dữ liệu filter)
  let vParamsQuery = getAllParamsInQueryString();
  fillFormWithQueryParams(vParamsQuery);
  //tạo page with filter and search(giữ trạng thái page đang hiển thị)
  vParamsQuery.page ? createPage(vParamsQuery.page + 1) : createPage(1);
}

// Hàm bấm nút tìm kiếm
function onBtnSearchClick() {
  const vKeyword = getAllParamsInQueryString().keyword;
  const vInputSearch = convertEmptyToNull($.trim($("#input-search").val()));

  // Chuyển cả keyword và input search về cùng một kiểu chữ (chữ thường)
  const vKeywordLower = vKeyword != null ? vKeyword.toLowerCase() : null;
  const vInputSearchLower =
    vInputSearch != null ? vInputSearch.toLowerCase() : null;

  // So sánh nếu cả hai giá trị giống nhau và không làm gì nếu chúng là giống nhau
  if (vKeywordLower !== vInputSearchLower) {
    if (vInputSearch != null) {
      let vKeywordParam = new URLSearchParams({ keyword: vInputSearch });
      window.location.href = gPROPERTIES_URL + "?" + vKeywordParam.toString();
    } else {
      window.location.href = gPROPERTIES_URL;
    }
  }
}

//Hàm bấm nút clear filter
function onBtnClearFilterClick() {
  clearFilter();
  createPage(1);
}

//Hàm click tags
function onClickTags(paramTag) {
  let vKeywordParam = new URLSearchParams({ keyword: paramTag.innerText });
  window.location.href = gPROPERTIES_URL + "?" + vKeywordParam.toString();
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
//hàm tạo filter pagination page
function createPage(paramPagenum) {
  //B0: tạo đối tượng lưu trữ dữ liệu filter
  let vDataFilterObj = {
    provinceId: null,
    type: null,
    request: null,
    furnitureType: null,
    direction: null,
    minPrice: null,
    maxPrice: null,
    minAcreage: null,
    maxAcreage: null,
  };
  //B1: Thu thập dữ liệu search and filter
  const vKeyword = getAllParamsInQueryString().keyword;
  collectDataFilter(vDataFilterObj);
  $(
    "#search-properties-section .result-container .spinner-pagination"
  ).removeClass("d-none");
  //B3: Call APi
  callAPISearchAndFilterRealEstatesPagination(
    vKeyword,
    vDataFilterObj,
    gNumberOfElements,
    paramPagenum
  );
}

//api filter
function callAPISearchAndFilterRealEstatesPagination(
  paramKeyword,
  paramDataFilterObj,
  paramNumberFilterOfElements,
  paramPagenum
) {
  // Create a new URLSearchParams object
  const vQueryParams = new URLSearchParams({
    page: paramPagenum - 1,
    size: paramNumberFilterOfElements,
  });

  // Add parameters to the URLSearchParams object if they are not null or undefined
  if (paramKeyword != null) vQueryParams.append("keyword", paramKeyword);
  if (paramDataFilterObj.provinceId !== null)
    vQueryParams.append("provinceId", paramDataFilterObj.provinceId);
  if (paramDataFilterObj.type !== null)
    vQueryParams.append("type", paramDataFilterObj.type);
  if (paramDataFilterObj.request !== null)
    vQueryParams.append("request", paramDataFilterObj.request);
  if (paramDataFilterObj.furnitureType !== null)
    vQueryParams.append("furnitureType", paramDataFilterObj.furnitureType);
  if (paramDataFilterObj.direction !== null)
    vQueryParams.append("direction", paramDataFilterObj.direction);
  if (paramDataFilterObj.minPrice !== null)
    vQueryParams.append("minPrice", paramDataFilterObj.minPrice);
  if (paramDataFilterObj.maxPrice !== null)
    vQueryParams.append("maxPrice", paramDataFilterObj.maxPrice);
  if (paramDataFilterObj.minAcreage !== null)
    vQueryParams.append("minAcreage", paramDataFilterObj.minAcreage);
  if (paramDataFilterObj.maxAcreage !== null)
    vQueryParams.append("maxAcreage", paramDataFilterObj.maxAcreage);

  // Lấy url htai
  let vCurrentUrl = window.location.href;

  // Thêm querystring vào url
  let vUpdatedUrl = vCurrentUrl.split("?")[0] + "?" + vQueryParams.toString();

  // Thay đổi url mà ko cần load lại page
  window.history.pushState({ path: vUpdatedUrl }, "", vUpdatedUrl);
  blockUI();
  $.ajax({
    type: "get",
    url: gBASE_URL + "/pagination/search-and-filter?" + vQueryParams.toString(),
    dataType: "json",
    success: function (paramData) {
      let vContainer = $("#search-properties-section .result-container");
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
    finally: unblockUI(),
  });
}

//api province
function callAPIGetAllProvinces() {
  return new Promise(function(resolve, reject) {
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

//Load data to select province
function loadDataToProvinceSelect(pamramProvinces) {
  for (let i = 0; i < pamramProvinces.length; i++) {
    let option = $("<option/>");
    option.prop("value", pamramProvinces[i].id);
    option.prop("text", pamramProvinces[i].name);
    $("#select-location").append(option);
  }
}

//Hàm xử lý data phân trang
function handleDataPagination(paramContainer, paramData, paramPagenum) {
  gTotalElements = paramData.totalElements; //lấy tổng số bản ghi
  gTotalPages = paramData.totalPages; //lấy tổng số page

  let vContentContainer = paramContainer.find(".content-container");
  displayDataGetFilterRealEstatesPagination(vContentContainer, paramData); //Gọi hàm xử lý hiển thị lấy danh sách phân trang

  if (paramData.totalElements > 0 && paramPagenum <= gTotalPages ) {
    let vPaginationContainer = paramContainer.find(".pagination-container");
    createPagination(vPaginationContainer, paramPagenum); // Gọi hàm tạo thanh phân trang
  } else {
    let vPaginationContainer = paramContainer.find(".pagination-container");
    vPaginationContainer.empty();
    vContentContainer.append(`
      <div class="col-md-6 text-center">
          <img src="assets/images/search/notfound.png" alt="Not Found" class="img-fluid" id="not-found-image">
          <h3 class="mt-3 text-secondary" id="not-found-message">No results found</h3>
      </div>
  `);
  }

  paramContainer.find(".spinner-pagination").addClass("d-none"); //Ẩn spinner
}

//Hàm xử lý hiển thị lấy danh sách records có phân trang
function displayDataGetFilterRealEstatesPagination(
  paramContentContainer,
  paramData
) {
  paramContentContainer.empty();
  if (paramData.totalElements > 0) {
    //Dùng Object.values đổi giá trị order trả về thành 1 mảng để dùng forEach
    let vRecordsArr = Object.values(paramData.content);
    if (vRecordsArr.length > 0) {
      paramContentContainer.append(`
      <p class="mt-1 mb-3 text-secondary">Found <span class="text-danger">${paramData.totalElements}</span> matching results</p>`);
      vRecordsArr.forEach((paramRecords) => {
        paramContentContainer.append(`
          <div class="col-12 col-md-6 col-lg-4 mb-5 d-flex justify-content-center">
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
}

// Hàm tạo thanh phân trang
function createPagination(paramPaginationContainer, paramPagenum) {
  paramPaginationContainer.empty();
  // Nếu tran hiện tại là trang 1 thì nút Prev sẽ bị disable
  if (paramPagenum == 1) {
    paramPaginationContainer.append(
      "<li class='page-item disabled previous mx-1'><a href='javascript:void(0)' class='page-link rounded-circle'><i class='fas fa-chevron-circle-left'></i></a></li>"
    );
  } else {
    paramPaginationContainer.append(
      "<li class='page-item mx-1' onclick='createPage(" +
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
      "<li class='page-item next mx-1' onclick='createPage(" +
        (paramPagenum + 1) +
        ")'><a href='javascript:void(0)' class='page-link rounded-circle'><i class='fas fa-chevron-circle-right'></i></a></li>"
    );
  }
}

//hàm lấy dữ liệu filter&search từ querystring
function getAllParamsInQueryString() {
  const params = new URLSearchParams(window.location.search);

  const paramDataFilterObj = {
    page: parseInt(params.get("page")),
    keyword: params.get("keyword"),
    provinceId: params.get("provinceId") || "",
    type: params.get("type") || "",
    request: params.get("request") || "",
    furnitureType: params.get("furnitureType") || "",
    direction: params.get("direction") || "",
    minPrice: params.get("minPrice") || "",
    maxPrice: params.get("maxPrice") || "",
    minAcreage: params.get("minAcreage") || "",
    maxAcreage: params.get("maxAcreage") || ""
  };

  return paramDataFilterObj;
}

//hàm lấy thông tin filter
function collectDataFilter(paramDataFilterObj) {
  paramDataFilterObj.provinceId = convertEmptyToNull(
    $.trim($("#select-location").val())
  );
  paramDataFilterObj.type = convertEmptyToNull($.trim($("#select-type").val()));
  paramDataFilterObj.request = convertEmptyToNull(
    $.trim($("#select-request").val())
  );
  paramDataFilterObj.furnitureType = convertEmptyToNull(
    $.trim($("#select-furniture-type").val())
  );
  paramDataFilterObj.direction = convertEmptyToNull(
    $.trim($("#select-direction").val())
  );

  let vMinPrice = parseInt($.trim($("#input-min-price").val()));
  paramDataFilterObj.minPrice = isNaN(vMinPrice) ? null : vMinPrice;

  let vMaxPrice = parseInt($.trim($("#input-max-price").val()));
  paramDataFilterObj.maxPrice = isNaN(vMaxPrice) ? null : vMaxPrice;

  let vMinAcreage = parseInt($.trim($("#input-min-acreage").val()));
  paramDataFilterObj.minAcreage = isNaN(vMinAcreage) ? null : vMinAcreage;

  let vMaxAcreage = parseInt($.trim($("#input-max-acreage").val()));
  paramDataFilterObj.maxAcreage = isNaN(vMaxAcreage) ? null : vMaxAcreage;
}

//Hàm điền queryparams vào form(lưu trạng thái filter)
function fillFormWithQueryParams(paramsQuery) {
  $("#input-search").val(paramsQuery.keyword);
  $("#select-location").val(paramsQuery.provinceId);
  $("#select-type").val(paramsQuery.type);
  $("#select-request").val(paramsQuery.request);
  $("#select-furniture-type").val(paramsQuery.furnitureType);
  $("#select-direction").val(paramsQuery.direction);
  $("#input-min-price").val(paramsQuery.minPrice);
  $("#input-max-price").val(paramsQuery.maxPrice);
  $("#input-min-acreage").val(paramsQuery.minAcreage);
  $("#input-max-acreage").val(paramsQuery.maxAcreage);
}

//hàm chuyển empty sang null để gửi dữ liệu lọc tới server ko bị lỗi
function convertEmptyToNull(paramValue) {
  return paramValue === "" ? null : paramValue;
}

//hàm clear filter
function clearFilter() {
  $("#select-location").val("");
  $("#select-type").val("");
  $("#select-request").val("");
  $("#select-furniture-type").val("");
  $("#select-direction").val("");
  $("#input-min-price").val("");
  $("#input-max-price").val("");
  $("#input-min-acreage").val("");
  $("#input-max-acreage").val("");
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
