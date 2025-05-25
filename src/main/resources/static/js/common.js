/**
 * 电商平台通用组件和工具函数
 */

// 当DOM加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 渲染导航栏
    renderNavbar();
    
    // 检查用户登录状态并更新UI
    checkLoginStatus();
});

/**
 * 渲染导航栏到指定的容器元素
 * @param {string} containerId - 导航栏容器的ID，默认为'navbar-container'
 */
function renderNavbar(containerId = 'navbar-container') {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error(`导航栏容器 #${containerId} 不存在`);
        return;
    }
    
    const currentPage = getCurrentPage();
    
    container.innerHTML = `
    <nav class="navbar navbar-expand-lg navbar-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="index.html">电商平台</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link ${currentPage === 'product-search' ? 'active' : ''}" href="product-search.html">商品搜索</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${currentPage === 'shopping-cart' ? 'active' : ''}" href="shopping-cart.html">购物车</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${currentPage === 'consumption-stats' ? 'active' : ''}" href="consumption-stats.html">消费统计</a>
                    </li>
                </ul>
                <div class="d-flex" id="userActions">
                    <a href="auth.html" class="btn btn-outline-light">登录/注册</a>
                </div>
            </div>
        </div>
    </nav>
    `;
}

/**
 * 检查用户登录状态并更新用户界面
 */
function checkLoginStatus() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    
    if (token && username) {
        // 更新用户下拉菜单
        updateUserDropdown(username);
        setupUserDropdownEvents();
        
        // 如果没有角色信息，从API获取
        if (!localStorage.getItem('userRole')) {
            axios.get('/api/user/info')
                .then(function(response) {
                    if (response.data && response.data.data) {
                        const userData = response.data.data;
                        localStorage.setItem('userRole', userData.role);
                        updateRoleBasedElements(userData.role);
                    }
                })
                .catch(function(error) {
                    console.error('获取用户信息失败:', error);
                });
        }
    }
}

/**
 * 更新用户下拉菜单
 * @param {string} username - 用户名
 */
function updateUserDropdown(username) {
    const userActions = document.getElementById('userActions');
    if (!userActions) return;
    
    const currentPage = getCurrentPage();
    
    userActions.innerHTML = `
    <div class="dropdown">
        <button class="btn btn-light dropdown-toggle d-flex align-items-center" type="button" id="userDropdown" 
            data-bs-toggle="dropdown" aria-expanded="false">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-person-circle me-1" viewBox="0 0 16 16">
                <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
                <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>
            </svg>
            <span id="username">${username}</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown" style="width: 200px;">
            <li><h6 class="dropdown-header">账户管理</h6></li>
            <li><a class="dropdown-item ${currentPage === 'account-info' ? 'active' : ''}" href="account-info.html">
                <i class="bi bi-person me-2"></i>账户信息
            </a></li>
            <li><a class="dropdown-item ${currentPage === 'address-list' ? 'active' : ''}" href="address-list.html">
                <i class="bi bi-geo-alt me-2"></i>地址管理
            </a></li>
            <li><a class="dropdown-item ${currentPage === 'order-list' ? 'active' : ''}" href="order-list.html">
                <i class="bi bi-receipt me-2"></i>我的订单
            </a></li>
            <li><a class="dropdown-item ${currentPage === 'favorites' ? 'active' : ''}" href="favorites.html">
                <i class="bi bi-heart me-2"></i>我的收藏
            </a></li>
            <li><a class="dropdown-item ${currentPage === 'security' ? 'active' : ''}" href="security.html">
                <i class="bi bi-shield-lock me-2"></i>安全设置
            </a></li>
            <li class="normal-user-only" style="display: none;"><a class="dropdown-item ${currentPage === 'vip-upgrade' ? 'active' : ''}" href="vip-upgrade.html">
                <i class="bi bi-star me-2"></i>成为VIP
            </a></li>
            <li class="admin-only" style="display: none;"><a class="dropdown-item ${currentPage === 'admin' ? 'active' : ''}" href="admin/index.html">
                <i class="bi bi-gear me-2"></i>系统管理
            </a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#" id="navLogoutBtn">
                <i class="bi bi-box-arrow-right me-2"></i>退出登录
            </a></li>
        </ul>
    </div>
    `;
    
    // 获取用户角色并更新UI
    updateUIByRole();
}

/**
 * 设置用户下拉菜单事件
 */
function setupUserDropdownEvents() {
    // 退出登录
    const logoutBtn = document.getElementById('navLogoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    }
}

/**
 * 退出登录
 */
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('userRole');
    window.location.href = 'auth.html';
}

/**
 * 根据用户角色更新UI
 */
function updateUIByRole() {
    const userRole = localStorage.getItem('userRole');
    
    if (!userRole) {
        // 如果本地没有角色信息，获取用户信息
        axios.get('/api/user/info')
            .then(function(response) {
                if (response.data && response.data.data) {
                    const userData = response.data.data;
                    localStorage.setItem('userRole', userData.role);
                    updateRoleBasedElements(userData.role);
                }
            })
            .catch(function(error) {
                console.error('获取用户信息失败:', error);
            });
    } else {
        updateRoleBasedElements(userRole);
    }
}

/**
 * 更新基于角色的元素显示
 * @param {string|number} role - 用户角色
 */
function updateRoleBasedElements(role) {
    // 管理员特权功能
    const adminElements = document.querySelectorAll('.admin-only');
    // VIP特权功能
    const vipElements = document.querySelectorAll('.vip-only');
    // 普通用户功能
    const normalUserElements = document.querySelectorAll('.normal-user-only');
    
    if (role === 2 || role === '2') { // 管理员
        // 显示管理员元素
        adminElements.forEach(el => el.style.display = 'block');
        // 显示VIP元素（管理员拥有所有权限）
        vipElements.forEach(el => el.style.display = 'block');
        // 隐藏普通用户元素
        normalUserElements.forEach(el => el.style.display = 'none');
    } else if (role === 1 || role === '1') { // VIP用户
        // 隐藏管理员元素
        adminElements.forEach(el => el.style.display = 'none');
        // 显示VIP元素
        vipElements.forEach(el => el.style.display = 'block');
        // 隐藏普通用户元素
        normalUserElements.forEach(el => el.style.display = 'none');
    } else { // 普通用户 (role === 0 || role === '0')
        // 隐藏管理员元素
        adminElements.forEach(el => el.style.display = 'none');
        // 隐藏VIP元素
        vipElements.forEach(el => el.style.display = 'none');
        // 显示普通用户元素
        normalUserElements.forEach(el => el.style.display = 'block');
    }
}

/**
 * 获取当前页面名称
 * @returns {string} 当前页面的基本名称（不含扩展名）
 */
function getCurrentPage() {
    const path = window.location.pathname;
    // 处理带有/static/前缀或其他路径的情况
    const filename = path.substring(path.lastIndexOf('/') + 1);
    return filename.replace(/\.html$/, '');
}

/**
 * 显示提示信息
 * @param {string} type - 提示类型: 'success' 或 'error'
 * @param {string} message - 提示消息
 * @param {string} containerId - 提示容器的ID
 */
function showAlert(type, message, containerId = 'alertContainer') {
    const alertContainer = document.getElementById(containerId);
    if (!alertContainer) return;
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type === 'error' ? 'danger' : 'success'} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertDiv);
    
    // 5秒后自动关闭
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => {
            if (alertContainer.contains(alertDiv)) {
                alertContainer.removeChild(alertDiv);
            }
        }, 150);
    }, 5000);
} 