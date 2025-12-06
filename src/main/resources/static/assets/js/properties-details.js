"use strict";
/*** REGION 1 - Global letiables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
let gLastScrollTop = 0; // Biến lưu trữ vị trí cuộn trước đó
let gScrolledDown = false; // Biến kiểm tra người dùng đã cuộn xuống chưa

let gHOME_URL = "index.html";
let gPROPERTIES_DETAILS_URL = "properties-details.html";
let gUPDATE_PROPERTIES_URL = "update-properties.html";
//URL API
let gBASE_URL = "/real-estates";
let gAUTH_URL = "/auth";
let gLOGIN_URL = "login.html";

//Biến lưu trữ thông tin
let gNumberOfElements = 4; //số bản ghi filter 1 page
let gTotalElements; //tổng số bản ghi
let gTotalPages; //tổng số page
let gSimilarObj = {
  type: null,
  request: null
}

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

  // update property
  $(".property-container").on("click", "#modify-property .update-property", onBtnUpdateProperty);

  // delete property
  $(".property-container").on("click", "#modify-property .delete-property", onBtnDeleteProperty);

  //Event click btn confirm delete
  $("#btn-confirm-delete").click(onBtnConfirmDeleteProperty);

  // complete property
  $(".property-container").on("click", "#modify-property .complete-property", onBtnCompleteProperty);

  //Event click btn confirm delete
  $("#btn-confirm-complete").click(onBtnConfirmCompleteProperty);

  // restore property
  $(".property-container").on("click", "#modify-property .restore-property", onBtnRestoreProperty);

  //Event click btn confirm restore
  $("#btn-confirm-restore").click(onBtnConfirmRestoreProperty);

  // like property
  $(".property-container").on("click", "#like-property .unlike-property", onBtnLikeProperty);

  // unlike property
  $(".property-container").on("click", "#like-property .like-property", onBtnUnLikeProperty);
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
async function onPageLoading() {
  let vId = getIdInQueryString();
  const accessToken = getAccessToken();

  if (vId != null) {
    await callAPIGetRealEstateById(vId);
    accessToken ? callAPICheckFavourite(vId, accessToken) : null;
  } else {
    window.location.href = gLOGIN_URL;
  }

  if (accessToken) {
    $("#login-icon").addClass("d-none")
    $("#user-icon").removeClass("d-none")
    $("#cart-icon").removeClass("d-none")
    callAPIVerifyUser(accessToken);
    callAPIVerifyAdmin(accessToken);
  }
}

function createPage(paramPagenum) {
  $("#similar-properties-section .content-container .spinner-pagination").removeClass("d-none");
  callAPIGetRealEstateSimilar(gSimilarObj, gNumberOfElements, paramPagenum);
}

// Hàm get detail
function getDetailsClick(paramCard) {
  let vId = $(paramCard).find(".card-id").text();
  let vIdParam = new URLSearchParams({ id: vId });
  window.location.href = gPROPERTIES_DETAILS_URL + "?" + vIdParam.toString();
}

// Hàm update
function onBtnUpdateProperty() {
  //check id
  const vId = getIdInQueryString();
  window.location.href = gUPDATE_PROPERTIES_URL + "?id=" + vId;
}

// Hàm delete
function onBtnDeleteProperty() {
  //check id
  const vId = getIdInQueryString();
  $("#delete-modal").modal("show");
}

// Hàm confirm delete
function onBtnConfirmDeleteProperty() {
  //check id
  const vId = getIdInQueryString();
  const accessToken = getAccessToken();
  callAPIDeleteRealEstateById(vId, accessToken);
}

// Hàm complete
function onBtnCompleteProperty() {
  //check id
  const vId = getIdInQueryString();
  $("#complete-modal").modal("show");
}

// Hàm confirm complete
function onBtnConfirmCompleteProperty() {
  //check id
  const vId = getIdInQueryString();
  const accessToken = getAccessToken();
  callAPICompleteRealEstateById(vId, accessToken);
}

// Hàm restore
function onBtnRestoreProperty() {
  //check id
  const vId = getIdInQueryString();
  $("#restore-modal").modal("show");
}

// Hàm confirm restore
function onBtnConfirmRestoreProperty() {
  //check id
  const vId = getIdInQueryString();
  const accessToken = getAccessToken();
  callAPIRestoreRealEstateById(vId, accessToken);
}

// Hàm like property
function onBtnLikeProperty() {
  $(".property-container #like-property .unlike-property").addClass("d-none");
  $(".property-container #like-property .like-property").removeClass("d-none");
  const vId = getIdInQueryString();
  const accessToken = getAccessToken();
  callAPIAddFavouriteProperty(vId, accessToken);
}

// Hàm like property
function onBtnUnLikeProperty() {
  $(".property-container #like-property .unlike-property").removeClass("d-none");
  $(".property-container #like-property .like-property").addClass("d-none");
  const vId = getIdInQueryString();
  const accessToken = getAccessToken();
  callAPIRemoveFavouriteProperty(vId, accessToken);
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//API
function callAPIGetRealEstateById(paramId) {
  return new Promise((resolve, reject) => {
    blockUI();
    $.ajax({
      type: "get",
      url: gBASE_URL + "/" + paramId,
      dataType: "json",
      success: function (paramData) {
        handleDataRealEstateDetails(paramData);
        gSimilarObj.type = paramData.type;
        gSimilarObj.request = paramData.request;
        createPage(1);
        resolve(paramData);
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

function callAPIGetRealEstateSimilar(
  paramData,
  gNumberOfElements,
  paramPagenum
) {
  // Create a new URLSearchParams object
  const vQueryParams = new URLSearchParams({
    page: paramPagenum - 1,
    size: gNumberOfElements,
  });

  // Add parameters to the URLSearchParams object if they are not null or undefined
  if (paramData.type !== null)
    vQueryParams.append("type", paramData.type);
  if (paramData.request !== null)
    vQueryParams.append("request", paramData.request);
  blockUI();
  $.ajax({
    type: "get",
    url: gBASE_URL + "/pagination/search-and-filter?" + vQueryParams.toString(),
    dataType: "json",
    success: function (paramData) {
      let vContainer = $("#similar-properties-section");
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

// Hàm này gửi yêu cầu API để chuyển đổi địa chỉ thành tọa độ sử dụng dịch vụ Photon
function callAPIGeocodeAddress(paramAddress) {
  // Trả về một Promise để xử lý kết quả từ API geocoding
  return new Promise(function(resolve, reject) {
    // Xây dựng URL cho yêu cầu geocoding, mã hóa địa chỉ để phù hợp với định dạng URL
    let photonURL = `https://photon.komoot.io/api/?q=${encodeURIComponent(paramAddress)}`;
    blockUI();
    // Sử dụng AJAX để gửi yêu cầu GET đến API geocoding
    $.ajax({
      url: photonURL, // URL của API Photon
      type: 'GET',
      dataType: 'json', // Yêu cầu dữ liệu trả về dưới dạng JSON
      success: function(data) { // Hàm này được gọi khi yêu cầu thành công
        if (data.features.length > 0) { // Kiểm tra xem có kết quả trả về không
          let location = data.features[0].geometry.coordinates; // Lấy tọa độ từ kết quả đầu tiên
          // Giải quyết Promise với tọa độ vị trí (latitude và longitude)
          resolve({
            lat: location[1], // Vĩ độ
            lon: location[0]  // Kinh độ
          });
        } else {
          showToast(4, "Address not found"); // Hiển thị thông báo khi không tìm thấy địa chỉ
        }
      },
      error: function(error) { // Hàm này được gọi khi có lỗi xảy ra trong quá trình gửi yêu cầu
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
      //check danh tính ng dùng
      let username = $(".employee-container .card-info-title").text();
      if (username === paramData.username) {
        $("#modify-property").removeClass("d-none");
      } else {
        $("#like-property").removeClass("d-none");
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

//api check favourite
function callAPICheckFavourite(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gAUTH_URL + "/isfavourite/" + paramId,
    method: "GET",
    headers: headers,
    success: function(paramData) {
      if(paramData) {
        $(".like-property").removeClass("d-none");
      } else {
        $(".unlike-property").removeClass("d-none");
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

//api add favourite
function callAPIAddFavouriteProperty(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gAUTH_URL + "/favourite/" + paramId,
    method: "POST",
    headers: headers,
    success: function(paramData) {
      showToast(1, "Property added to favorites successfully");
    },
    error: function(error) {
      $(".unlike-property").removeClass("d-none");
      $(".like-property").addClass("d-none");

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

//api remove favourite
function callAPIRemoveFavouriteProperty(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gAUTH_URL + "/favourite/" + paramId,
    method: "PUT",
    headers: headers,
    success: function(paramData) {
      showToast(1, "Property removed to favorites successfully");
    },
    error: function(error) {
      $(".unlike-property").addClass("d-none");
      $(".like-property").removeClass("d-none");

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

function callAPIDeleteRealEstateById(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/" + paramId,
    method: "DELETE",
    headers: headers,
    success: function (res) {
      $("#delete-modal").modal("hide");
      showToast(1, "Delete Property successfully!");
      setTimeout(() => {
        history.back();
        unblockUI();
      }, 1000);
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

function callAPICompleteRealEstateById(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/complete/" + paramId,
    method: "POST",
    headers: headers,
    success: function (res) {
      $("#complete-modal").modal("hide");
      showToast(1, "Complete Property successfully!");
      setTimeout(() => {
        history.back();
        unblockUI();
      }, 1000);
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

function callAPIRestoreRealEstateById(paramId, paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/restore/" + paramId,
    method: "POST",
    headers: headers,
    success: function (res) {
      $("#restore-modal").modal("hide");
      showToast(1, "Restore Property successfully!");
      setTimeout(() => {
        history.back();
        unblockUI();
      }, 1000);
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

//handle
function handleDataRealEstateDetails(paramData) {
  if (paramData) {
    //thêm property details
    $(".property-container").append(`
      <div class="estate-info-card card">
        <div class="estate-card-top">
          ${createCarousel(paramData)}
        </div>
        <div class="card-body">
          <p class="property-id d-none">${paramData.id}</p>
          <p class="employee-id d-none">${paramData.employeeId}</p>
          <p class="property-title">${paramData.title}</p>
          <div class="d-flex justify-content-start">
            <p class="card-info-title">${paramData.price} milion VND - ${paramData.acreage}m2 - ${paramData.request}</p>
          </div>
          <div class="d-flex justify-content-start">
            <i class="fa-solid fa-location-dot"></i>
            <p class="card-attribute "> &nbsp; <span class="text-decoration-underline">${paramData.address}</span></p>
          </div>
          <div class="d-flex justify-content-start">
            <i class="fa-regular fa-clock"></i>
            <p class="card-attribute"> &nbsp; Added: ${paramData.createdAt}</p>
          </div>
          <div class="d-flex justify-content-start">
            <i class="fa-solid fa-shield"></i>
            <p class="card-attribute"> &nbsp; The property has been verified.</p>
          </div>
          <div class="d-flex justify-content-start mt-1 mb-1 d-none" style="gap: 1rem" id="modify-property">
            <i class="fa-solid fa-pen-to-square fa-xl update-property"></i>
            <i class="fa-solid fa-trash-can fa-xl delete-property"></i>
            <i class="fa-solid fa-square-check fa-xl complete-property"></i>
            ${paramData.status !== "APPROVED" && paramData.status !== "PENDING" ? '<i class="fa-solid fa-window-restore fa-xl restore-property"></i>' : ''}
          </div>
          <div class="d-flex justify-content-start mt-1 mb-1 d-none" style="gap: 1rem" id="like-property">
            <i class="fa-regular fa-heart fa-xl unlike-property d-none"></i>
            <i class="fa-solid fa-heart fa-xl like-property d-none"></i>
          </div>
        </div>
        <div class="card-footer">
          <p class="card-info-title">Property features</p>
          <div class="d-flex justify-content-between">
            <div class="col-6">
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-house"></i> &nbsp; Type: ${paramData.type}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-code-pull-request"></i> &nbsp; Request: ${paramData.request}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-vector-square"></i> &nbsp; Acreage: ${paramData.acreage}m2</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-map-location"></i> &nbsp; Province: ${paramData.provinceName !== null ? paramData.provinceName : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-map-location"></i> &nbsp; District: ${paramData.districtName !== null ? paramData.districtName : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-map-location"></i> &nbsp; Ward: ${paramData.wardName !== null ? paramData.wardName : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-road"></i> &nbsp; Street: ${paramData.streetName !== null ? paramData.streetName : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-building"></i> &nbsp; Project: ${paramData.projectName !== null ? paramData.projectName : ""}</p></div>
            </div>
            <div class="col-6">
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-dollar-sign"></i> &nbsp; Price/m2: ${(paramData.price / paramData.acreage).toFixed(3)} mil/m2</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-dollar-sign"></i> &nbsp; Price Rent: ${paramData.priceRent !== null ? (paramData.priceRent / 1000).toFixed(3) : ""} mil VND</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-compass"></i> &nbsp; Direction: ${paramData.direction !== null ? paramData.direction : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-signs-post"></i> &nbsp; Apartment Code: ${paramData.apartCode !== null ? paramData.apartCode : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-bed"></i> &nbsp; Bedroom: ${paramData.bedroom}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-solid fa-couch"></i> &nbsp; Furniture Type: ${paramData.furnitureType}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-regular fa-clock"></i> &nbsp; Date Created: ${paramData.createdAt !== null ? paramData.createdAt : ""}</p></div>
              <div class="d-flex justify-content-start"><p class="card-attribute"><i class="fa-regular fa-clock"></i> &nbsp; Last Updated: ${paramData.updatedAt !== null ? paramData.updatedAt : ""}</p></div>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <p class="card-info-title">Detailed description</p>
          <p class="card-description">${paramData.description !== null ? paramData.description : ""}<br>
          Contact the owner directly for more detailed information.
          </p>
        </div>
      </div>
    `);
    //thêm employee details
    $(".employee-container").append(`
      <div class="card">
        <img src="${paramData.employeePhoto != null ? paramData.employeePhoto : "assets/images/user/user_avatar.png"}" class="card-img-top img-thumbnail mx-auto" style="width: 50px; height: 50px; border-radius: 50%" alt="user image">
        <div class="card-body">
          <h5 class="card-info-title text-center">${paramData.employeeUserName != null ? paramData.employeeUserName : ""}</h5>
          <p class="card-text"><i class="fa-regular fa-envelope"></i> &nbsp Email: ${paramData.employeeEmail != null ? paramData.employeeEmail : ""}</p>
          <p class="card-text"><i class="fa-solid fa-phone-volume"></i> &nbsp Tel: ${paramData.employeeHomePhone != null ? paramData.employeeHomePhone : ""}</p>
          <p class="card-text">Please contact the seller via email or phone number.</p>
        </div>
      </div>
    `);
      //thêm map-container
      $(".employee-container").append(`
        <div id="map" style="height: 500px; border-radius: 0.375rem; flex-grow: 1"></div>
      `);
      //gọi hàm tạo map
      createMap(paramData.address);
  }
}

//Hàm xử lý data phân trang
function handleDataPagination(paramContainer, paramData, paramPagenum) {
  gTotalElements = paramData.totalElements; //lấy tổng số bản ghi
  gTotalPages = paramData.totalPages; //lấy tổng số page

  let vContentContainer = paramContainer.find(".content-container");
  displayDataGetFilterRealEstatesPagination(vContentContainer, paramData); //Gọi hàm xử lý hiển thị lấy danh sách phân trang

  if (paramData.totalElements > 0) {
    let vPaginationContainer = paramContainer.find(".pagination-container");
    createPagination(vPaginationContainer, paramPagenum); // Gọi hàm tạo thanh phân trang
  } else {
    let vPaginationContainer = paramContainer.find(".pagination-container");
    vPaginationContainer.empty();
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
      <p class="mt-1 mb-3 text-secondary text-center">There are <span class="text-danger">${paramData.totalElements}</span> similar results.</p>`);
      vRecordsArr.forEach((paramRecords) => {
        paramContentContainer.append(`
          <div class="col-12 col-md-6 col-lg-3 mb-5 d-flex justify-content-center">
              <div class="estate-card card" style="width: 20rem" onclick="getDetailsClick(this)">
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
  } else {
    paramContentContainer.append(`
    <div class="col-md-6 text-center">
        <img src="assets/images/search/notfound.png" alt="Not Found" class="img-fluid" id="not-found-image">
        <h3 class="mt-3 text-secondary" id="not-found-message">No results similar found</h3>
    </div>
`);
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

// Hàm tạo carousel từ dữ liệu đầu vào
function createCarousel(paramData) {
  // Chuỗi HTML cho carousel
  let carouselHTML = `
    <div id="carousel-property-image" class="carousel slide" data-bs-ride="carousel">
      <!-- Chỉ số -->
      <div class="carousel-indicators">
  `;

  // Thêm các chỉ số vào chuỗi HTML
  paramData.photosUrl.forEach((photoUrl, index) => {
    carouselHTML += `
      <!-- Nút chỉ số cho slide ${index} -->
      <button type="button" data-bs-target="#carousel-property-image" data-bs-slide-to="${index}" ${index === 0 ? 'class="active"' : ''}></button>
      `;
    });

    carouselHTML += `
      </div> <!-- /carousel-indicators -->
      
      <!-- Các slide -->
      <div class="carousel-inner">
  `;

  // Thêm các slide vào chuỗi HTML
  paramData.photosUrl.forEach((photoUrl, index) => {
    carouselHTML += `
      <!-- Slide ${index} -->
      <div class="carousel-item ${index === 0 ? 'active' : ''}">
        <img src="${photoUrl}" class="d-block w-100" alt="Estate Image">
      </div>
    `;
  });

  carouselHTML += `
      </div> <!-- /carousel-inner -->

      <!-- Nút điều khiển Trước và Sau -->
      <button class="carousel-control-prev" type="button" data-bs-target="#carousel-property-image" data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Trước</span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#carousel-property-image" data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Tiếp</span>
      </button>
    </div> <!-- /carousel -->
  `;

  // Trả về chuỗi HTML của carousel
  return carouselHTML;
}

// Hàm tạo map từ địa chỉ
async function createMap(paramAddress) {
  // Khởi tạo bản đồ với vị trí mặc định
  let map = L.map('map').setView([44.861051, -74.295628], 15); // Vị trí mặc định (có thể thay đổi)

  // Thêm lớp bản đồ OpenStreetMap
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map);
  // Gọi hàm callAPIGeocodeAddress để thực hiện geocoding cho địa chỉ được cung cấp (paramAddress)
  callAPIGeocodeAddress(paramAddress)
    .then(location => {
        // Nếu geocoding thành công, lấy tọa độ (latitude và longitude) từ location
        let lat = location.lat;
        let lon = location.lon;
        
        // Cập nhật vị trí của bản đồ với vị trí mới và mức độ phóng to
        map.setView([lat, lon], 15);
        
        // Thêm một đánh dấu tại vị trí mới trên bản đồ và hiển thị thông tin địa chỉ trong một cửa sổ popup
        L.marker([lat, lon]).addTo(map)
            .bindPopup(paramAddress) // Hiển thị thông tin địa chỉ
            .openPopup(); // Mở popup tự động  
        // Cập nhật kích thước của bản đồ sau khi tọa độ được thiết lập
        // do Leaflet cần phải được thông báo về thay đổi kích thước để nó có thể cập nhật bản đồ cho phù hợp
        map.invalidateSize();
    })
    .catch(error => showToast(3, error)); // Xử lý lỗi nếu có
}

//hàm lấy id từ querystring
function getIdInQueryString() {
  const params = new URLSearchParams(window.location.search);
  const vId = params.get("id");
  return vId;
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
