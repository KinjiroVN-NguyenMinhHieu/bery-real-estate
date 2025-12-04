"use strict";
/*** REGION 1 - Global variables - Vùng khai báo biến, hằng số, tham số TOÀN CỤC */
const gLOGIN_URL = "../login.html";
const gHOME_URL = "../index.html";

//URL API
const gBASE_URL = window.location.origin;
const gAUTH_URL = "/auth";

// Sử dụng jQuery UI để làm cho các widget trên dashboard có thể sắp xếp được
$('.connectedSortable').sortable({
  placeholder: 'sort-highlight',
  connectWith: '.connectedSortable',
  handle: '.card-header, .nav-tabs',
  forcePlaceholderSize: true,
  zIndex: 999999
})
$('.connectedSortable .card-header').css('cursor', 'move')

/* Chart.js Charts */
var salesChartCanvas = $('#revenue-chart-canvas').get(0).getContext('2d');

var initialData = {
  labels: [], // Bắt đầu với nhãn trống
  datasets: [] // Bắt đầu với dữ liệu trống
};

var salesChartOptions = {
  maintainAspectRatio: false,
  responsive: true,
  legend: {
    display: true
  },
  scales: {
    xAxes: [{
      gridLines: {
        display: false
      }
    }],
    yAxes: [{
      gridLines: {
        display: false
      }
    }]
  }
};

var salesChart = new Chart(salesChartCanvas, {
  type: 'line',
  data: initialData,
  options: salesChartOptions
});

// Khởi tạo biểu đồ pie (hoặc donut) mới
var pieChartCanvas = $('#sales-chart-canvas').get(0).getContext('2d');

var pieOptions = {
  legend: {
    display: true // Hiển thị legend cho pie chart
  },
  maintainAspectRatio: false,
  responsive: true
};

var pieChart = new Chart(pieChartCanvas, {
  type: 'doughnut', // Hoặc 'pie' nếu bạn muốn biểu đồ hình tròn
  data: transformPieData([]), // Khởi tạo với dữ liệu rỗng, sẽ cập nhật sau khi có dữ liệu
  options: pieOptions
});

/*** REGION 2 - Vùng gán / thực thi hàm xử lý sự kiện cho các elements */
$(document).ready(function () {
  onPageLoading();

  // đăng xuất
  $("#logout-icon").click(() => {
    onBtnLogout();
  });
});

/*** REGION 3 - Event handlers - Vùng khai báo các hàm xử lý sự kiện */
function onPageLoading() {
  const accessToken = getAccessToken();
  if (accessToken) {
    callAPIVerifyAdmin(accessToken);
    callAPIVerifyUser(accessToken);
  } else {
    window.location.href = gLOGIN_URL;
  }
  callAPICountPendingRealEstates(accessToken);
  callAPICountPercentApprovedRealEstates(accessToken);
  callAPICountAllEmployees(accessToken);
  callAPICountAllCustomers(accessToken);
  callAPIGetAllRealEstates();
}

// Hàm logout
function onBtnLogout() {
  const accessToken = getAccessToken();
  callAPILogout(accessToken);
}

/*** REGION 4 - Common funtions - Vùng khai báo hàm dùng chung trong toàn bộ chương trình*/
//Call API
function callAPIGetAllRealEstates() {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + getAccessToken()
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/all",
    method: "GET",
    headers: headers,
    success: function (paramData) {
      // Chuyển đổi dữ liệu cho biểu đồ line
      var transformedData = transformData(paramData);

      salesChart.data.labels = transformedData.labels;
      salesChart.data.datasets = transformedData.datasets;
      salesChart.update();
      
      // Chuyển đổi dữ liệu cho biểu đồ pie
      var pieTransformedData = transformPieData(paramData);
      
      pieChart.data.labels = pieTransformedData.labels;
      pieChart.data.datasets = pieTransformedData.datasets;
      pieChart.update();
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

//api count
function callAPICountPendingRealEstates(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/count/pending",
    method: "GET",
    headers: headers,
    success: function(paramData) {
      handleDataToSmallBoxes(paramData, 1);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPICountPercentApprovedRealEstates(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/real-estates/count/percent",
    method: "GET",
    headers: headers,
    success: function(paramData) {
      handleDataToSmallBoxes(paramData, 2);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPICountAllEmployees(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/employees/count",
    method: "GET",
    headers: headers,
    success: function(paramData) {
      handleDataToSmallBoxes(paramData, 3);
    },
    error: function(error) {
      handleError(error);
    },
    finally: unblockUI(),
  });
}

function callAPICountAllCustomers(paramAccessToken) {
  //Khai báo xác thực ở headers
  let headers = {
    Authorization: "Bearer " + paramAccessToken
  };
  blockUI();
  $.ajax({
    url: gBASE_URL + "/customers/count",
    method: "GET",
    headers: headers,
    success: function(paramData) {
      handleDataToSmallBoxes(paramData, 4);
    },
    error: function(error) {
      handleError(error);
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
      if(!paramData) {
        window.location.href = gHOME_URL;
      }
    },
    error: function(error) {
      handleError(error);
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
      $(".user-name").html(paramData.username);
      $("#cart-icon .badge").html(paramData.realEstatesCount);
    },
    error: function(error) {
      handleError(error);
      resetLogin();
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
      handleError(error);
    },
    finally: unblockUI(),
  });
}

// Hàm đổ dữ liệu vào small boxes
function handleDataToSmallBoxes(paramData, paramIndex) {
  // Chọn phần tử div theo index và thay đổi nội dung của phần tử <span> bên trong nó
  $("#small-boxes>div").eq(paramIndex - 1).find("span").html(paramData);
}

// Hàm chuyển đổi dữ liệu để đổ vào line chart
function transformData(data) {
  var labels = []; // Mảng lưu trữ nhãn cho trục X của biểu đồ (các ngày theo định dạng dd-MM-yyyy)
  var approvedData = []; // Mảng lưu trữ dữ liệu cho các bất động sản được phê duyệt
  var completedData = []; // Mảng lưu trữ dữ liệu cho các bất động sản hoàn thành

  // Đếm số lượng các trạng thái theo ngày
  var countMap = {}; // Đối tượng dùng để lưu trữ số lượng các trạng thái theo ngày

  // Duyệt qua từng mục trong dữ liệu
  data.forEach(function(item) {
    // Sử dụng updatedAt nếu có, nếu không có thì sử dụng createdAt
    var dateString = item.updatedAt || item.createdAt;

    // Nếu ngày là null hoặc undefined, bỏ qua bản ghi đó
    if (!dateString) {
      return;
    }

    // Chuyển đổi ngày từ chuỗi thành đối tượng Date theo định dạng dd-MM-yyyy
    var dateParts = dateString.split("-"); // Tách chuỗi ngày thành mảng [dd, MM, yyyy]
    var day = parseInt(dateParts[0], 10); // Lấy ngày
    var month = parseInt(dateParts[1], 10); // Lấy tháng
    var year = parseInt(dateParts[2], 10); // Lấy năm

    // Tạo đối tượng Date từ các phần đã tách
    var date = new Date(year, month - 1, day); // Tháng trong đối tượng Date bắt đầu từ 0, nên cần -1

    // Chuyển đổi ngày từ định dạng dd-MM-yyyy thành yyyy-MM-dd để sắp xếp
    var formattedDateForSorting = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;

    if (!countMap[formattedDateForSorting]) {
      countMap[formattedDateForSorting] = {
        approved: 0,
        completed: 0
      };
    }

    // Tăng số lượng tùy theo trạng thái của bất động sản
    if (item.status === 'APPROVED') {
      countMap[formattedDateForSorting].approved += 1;
    } else if (item.status === 'COMPLETED') {
      countMap[formattedDateForSorting].completed += 1;
    }
  });

  // Chuyển đổi countMap thành arrays labels và data để đổ vào biểu đồ
  var sortedDates = Object.keys(countMap)
    .map(date => ({
      date: date,
      dateObj: new Date(date) // Tạo đối tượng Date từ định dạng yyyy-MM-dd
    }))
    .sort((a, b) => a.dateObj - b.dateObj) // Sắp xếp theo đối tượng Date
    .map(item => item.date); // Lấy danh sách ngày đã sắp xếp

  sortedDates.forEach(date => {
    var dateParts = date.split("-"); // Tách chuỗi ngày thành mảng [yyyy, MM, dd]
    var day = dateParts[2]; // Lấy ngày
    var month = dateParts[1]; // Lấy tháng
    var year = dateParts[0]; // Lấy năm

    // Chuyển đổi ngày từ yyyy-MM-dd thành dd-MM-yyyy để hiển thị
    var formattedDateForDisplay = `${day}-${month}-${year}`;
    labels.push(formattedDateForDisplay); // Thêm ngày-tháng-năm theo định dạng dd-MM-yyyy vào nhãn
    approvedData.push(countMap[date].approved); // Thêm số lượng 'APPROVED' vào dữ liệu
    completedData.push(countMap[date].completed); // Thêm số lượng 'COMPLETED' vào dữ liệu
  });

  // Trả về đối tượng chứa nhãn và dữ liệu để đổ vào biểu đồ
  return {
    labels: labels,
    datasets: [
      {
        label: 'Approved',
        data: approvedData,
        backgroundColor: 'rgba(60,141,188,0.9)',
        borderColor: 'rgba(60,141,188,0.8)',
        pointRadius: false,
        pointColor: '#3b8bba',
        pointStrokeColor: 'rgba(60,141,188,1)',
        pointHighlightFill: '#fff',
        pointHighlightStroke: 'rgba(60,141,188,1)'
      },
      {
        label: 'Completed',
        data: completedData,
        backgroundColor: 'rgba(210, 214, 222, 1)',
        borderColor: 'rgba(210, 214, 222, 1)',
        pointRadius: false,
        pointColor: 'rgba(210, 214, 222, 1)',
        pointStrokeColor: '#c1c7d1',
        pointHighlightFill: '#fff',
        pointHighlightStroke: 'rgba(220,220,220,1)'
      }
    ]
  };
}

// Hàm chuyển đổi dữ liệu để đổ vào pie chart
function transformPieData(data) {
  var statusCount = {
    PENDING: 0,
    APPROVED: 0,
    REJECTED: 0,
    REMOVED: 0,
    COMPLETED: 0
  };

  // Đếm số lượng các trạng thái
  data.forEach(function(item) {
    if (statusCount.hasOwnProperty(item.status)) {
      statusCount[item.status] += 1;
    }
  });

  // Chuyển đổi thành định dạng dữ liệu cho biểu đồ pie
  return {
    labels: Object.keys(statusCount),
    datasets: [
      {
        data: Object.values(statusCount),
        backgroundColor: ['#f56954', '#00a65a', '#f39c12', '#00c0ef', '#3c8dbc'] // Màu sắc khác nhau cho từng trạng thái
      }
    ]
  };
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

//chuyển ngày dd-MM-yyyy thành yyyy-MM-dd(để trình duyệt hiểu)
function convertDateFormat(paramDateString) {
  if (paramDateString) {
    const dateParts = paramDateString.split("-");
    return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
  }
}
