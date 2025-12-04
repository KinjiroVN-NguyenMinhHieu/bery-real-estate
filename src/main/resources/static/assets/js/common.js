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

// Hàm xử lý lỗi
function handleError(error) {
  try {
    const responseObject = JSON.parse(error.responseText);
    showToast(3, responseObject.message);
  } catch (e) {
    showToast(3, error.responseText || error.statusText);
  }
}

// BlockUI
function blockUI() {
  $.blockUI({
    message: `
      <div class="d-flex align-items-center">
        <div class="spinner-border text-light me-2"></div>
        <span>Loading...</span>
      </div>
    `,
    css: {
      border: 'none',
      padding: '20px',
      backgroundColor: 'rgba(0,0,0,0.7)',
      color: '#fff',
      'border-radius': '8px'
    },
    overlayCSS: {
      position: 'fixed',   // quan trọng: fix overlay toàn viewport
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      backgroundColor: 'rgba(0,0,0,0.7)',
      zIndex: 9999
    }
  });
}

// unblock UI
function unblockUI() {
  $.unblockUI();
}