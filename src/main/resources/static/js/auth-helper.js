/**
 * 认证辅助工具
 * 用于管理所有页面的token处理和请求头设置
 */

// 立即执行函数，避免污染全局命名空间
(function() {
    // 初始化函数，在页面加载时调用
    function init() {
        console.log('Auth helper initialized');
        // 从URL参数中获取debug标志和来源信息
        const urlParams = new URLSearchParams(window.location.search);

        let token = urlParams.get('token');
        const username = urlParams.get('username');
        const userId = urlParams.get('userId');
        console.log("token, username, userId", token, username, userId);
        // 如果URL中包含登录信息，则保存到localStorage
        if (token && username && userId) {
            // 保存登录信息到localStorage
            localStorage.setItem('token', token);
            localStorage.setItem('username', username);
            localStorage.setItem('userId', userId);
        }


        // 立即设置token到 axios 默认请求头
        token = localStorage.getItem('token');
        if (token) {
            console.log('Setting token to axios default headers at init');
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            
            // 验证token状态
            setTimeout(function() {
                verifyToken();
            }, 100);
        } else {
            console.log('No token available at init');
        }
        
        // 设置拦截器
        setupAxiosInterceptors();
    }
    
    // 验证token是否有效
    function verifyToken() {
        console.log('Verifying token...');
        axios.get('/api/user/info')
            .then(function(response) {
                console.log('Token verification successful');
                // 如果成功，刷新token值
                const token = localStorage.getItem('token');
                if (token) {
                    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
                    console.log('Token refreshed in default headers');
                }
            })
            .catch(function(error) {
                console.error('Token verification failed:', error);
            });
    }
    
    // 设置Axios拦截器，确保每个请求都带有token
    function setupAxiosInterceptors() {
        const token = localStorage.getItem('token');
        if (!token) {
            console.log('No token found in localStorage');
            return;
        }
        
        console.log('Setting up axios interceptors with token');
        
        // 设置默认请求头
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        console.log('Set default Authorization header to:', `Bearer ${token.substring(0, 10)}...`);
        
        // 请求拦截器，确保每个请求都带有最新的token
        axios.interceptors.request.use(
            function(config) {
                // 每次请求前都从localStorage获取最新的token
                const currentToken = localStorage.getItem('token');
                if (currentToken) {
                    config.headers.Authorization = `Bearer ${currentToken}`;
                    console.log(`Request to ${config.url}: added token to headers`);
                    
                    // 设置cookie作为备份
                    document.cookie = `token=${currentToken}; path=/; max-age=86400`;
                }
                return config;
            },
            function(error) {
                return Promise.reject(error);
            }
        );
        
        // 响应拦截器，处理401错误
        axios.interceptors.response.use(
            function(response) {
                return response;
            },
            function(error) {
                // 如果是401错误，可能是token过期
                if (error.response && error.response.status === 401) {
                    console.log('Received 401 error, token might be expired');
                    
                    // 检查当前页面是否是auth.html
                    const isAuthPage = window.location.pathname.includes('auth.html');
                    
                    // 如果不是登录页面，而是其他页面收到401，考虑清除token并重定向
                    if (!isAuthPage) {
                        console.log('Not on auth page, clearing token and will redirect');
                        
                        // 但不要立即重定向，避免无限循环
                        const fromRedirect = window.location.search.includes('token_exists=true');
                        
                        if (!fromRedirect) {
                            console.warn('Token invalid but not from recent redirect, will clear token');
                            // 清除token
                            localStorage.removeItem('token');
                            localStorage.removeItem('username');
                            localStorage.removeItem('userId');
                            
                            // 添加失效提示而不是立即重定向
                            showTokenExpiredMessage();
                        } else {
                            console.warn('Token invalid after redirect, will not clear to avoid loop');
                            // 已经是重定向过来的，显示消息但不再继续重定向
                            showTokenExpiredMessage();
                        }
                    }
                }
                return Promise.reject(error);
            }
        );
    }
    
    // 显示token过期消息
    function showTokenExpiredMessage() {
        // 创建提示元素
        const msgEl = document.createElement('div');
        msgEl.className = 'alert alert-warning position-fixed top-0 start-50 translate-middle-x mt-3';
        msgEl.style.zIndex = '9999';
        msgEl.textContent = '登录已过期，请重新登录';
        
        // 创建登录按钮
        const loginBtn = document.createElement('a');
        loginBtn.href = '/static/auth.html';
        loginBtn.className = 'btn btn-primary btn-sm ms-3';
        loginBtn.textContent = '去登录';
        msgEl.appendChild(loginBtn);
        
        // 添加到页面
        document.body.appendChild(msgEl);
        
        // 5秒后自动隐藏
        setTimeout(() => {
            msgEl.style.opacity = '0';
            msgEl.style.transition = 'opacity 0.5s';
            setTimeout(() => msgEl.remove(), 500);
        }, 5000);
    }
    
    // 应用token到所有导航链接
    function setupNavigationLinks() {
        const token = localStorage.getItem('token');
        if (!token) return;
        
        console.log('Setting up navigation links with token protection');
        
        // 获取所有导航链接、侧边栏链接和功能卡片链接
        const navLinks = document.querySelectorAll('.nav-link, .dropdown-item, .feature-card');
        navLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const originalHref = this.getAttribute('href') || this.getAttribute('onclick');
                
                // 跳过退出登录按钮
                if (this.id === 'logoutBtn' || 
                    (originalHref && originalHref.includes('logout')) || 
                    !originalHref || 
                    originalHref === '#' || 
                    originalHref.startsWith('javascript')) {
                    return; // 正常执行退出登录或其他特殊操作
                }
                
                // 提取href (如果是onclick="location.href='...'"这种格式，则提取其中的URL)
                let targetUrl = originalHref;
                if (originalHref.startsWith('location.href=')) {
                    // 从onclick="location.href='...'"中提取URL
                    const match = originalHref.match(/location\.href=['"](.*?)['"]/);
                    if (match && match[1]) {
                        targetUrl = match[1];
                    }
                }
                
                console.log(`Navigation intercepted: ${targetUrl}`);
                
                // 添加token存在的标记参数
                if (targetUrl.includes('?')) {
                    window.location.href = `${targetUrl}&token_exists=true`;
                } else {
                    window.location.href = `${targetUrl}?token_exists=true`;
                }
            });
        });
        
        // 处理onclick属性中的地址跳转
        document.querySelectorAll('[onclick*="location.href"]').forEach(element => {
            if (element.classList.contains('nav-link') || element.classList.contains('feature-card')) {
                return; // 已经在上面处理过
            }
            
            const originalOnclick = element.getAttribute('onclick');
            
            // 不处理特殊操作
            if (originalOnclick.includes('logout') || !originalOnclick) {
                return;
            }
            
            // 提取URL
            const match = originalOnclick.match(/location\.href=['"](.*?)['"]/);
            if (match && match[1]) {
                const targetUrl = match[1];
                
                // 替换onclick属性
                element.setAttribute('onclick', '');
                element.addEventListener('click', function(e) {
                    e.preventDefault();
                    console.log(`Onclick navigation intercepted: ${targetUrl}`);
                    
                    // 添加token存在的标记参数
                    if (targetUrl.includes('?')) {
                        window.location.href = `${targetUrl}&token_exists=true`;
                    } else {
                        window.location.href = `${targetUrl}?token_exists=true`;
                    }
                });
            }
        });
    }
    
    // 验证用户登录状态并更新UI
    function validateLoginState() {
        const token = localStorage.getItem('token');
        const username = localStorage.getItem('username');
        
        // 如果有token和用户名，认为用户已登录
        if (token && username) {
            // 检查是否有角色信息，如果没有则从API获取
            const userRole = localStorage.getItem('userRole');
            if (!userRole) {
                // 获取用户信息和角色
                axios.get('/api/user/info')
                    .then(function(response) {
                        if (response.data && response.data.data) {
                            const userData = response.data.data;
                            localStorage.setItem('userRole', userData.role);
                            // 使用获取到的角色更新UI
                            updateUserInterface(username);
                        }
                    })
                    .catch(function(error) {
                        console.error('获取用户信息失败:', error);
                    });
            } else {
                // 使用已有的角色信息更新UI
                updateUserInterface(username);
            }
            
            // 更新UI显示已登录状态
            updateUserInterface(username);
            
            // 如果页面有特定的用户信息容器，更新它
            const userInfoEl = document.getElementById('userInfo');
            const usernameEl = document.getElementById('username');
            
            if (userInfoEl && usernameEl) {
                userInfoEl.style.display = 'block';
                usernameEl.textContent = username;
            }
            
            // 更新导航栏（如果存在）
            const userActionsEl = document.getElementById('userActions');
            if (userActionsEl) {
                userActionsEl.innerHTML = `
                    <div class="dropdown">
                        <button class="btn btn-light dropdown-toggle d-flex align-items-center" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-person-circle me-1" viewBox="0 0 16 16">
                                <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
                                <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>
                            </svg>
                            ${username}
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li><a class="dropdown-item" href="/static/user-center.html#address-list">地址管理</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="#" id="logoutBtn">退出登录</a></li>
                        </ul>
                    </div>
                `;
                
                // 添加退出登录按钮事件
                setTimeout(() => {
                    const logoutBtn = document.getElementById('logoutBtn');
                    if (logoutBtn) {
                        logoutBtn.addEventListener('click', logout);
                    }
                }, 0);
            }
            
            return true;
        }
        
        return false;
    }
    
    // 更新用户界面
    function updateUserInterface(username) {
        // 可以在这里添加特定页面的UI更新逻辑
        console.log(`Updating UI for user: ${username}`);
        
        // 获取用户角色并更新UI
        const userId = localStorage.getItem('userId');
        const userRole = localStorage.getItem('userRole');
        
        // 根据角色显示/隐藏元素
        updateUIByRole(userRole);
    }
    
    // 根据用户角色更新UI
    function updateUIByRole(role) {
        console.log(`Updating UI based on role: ${role}`);
        
        // 管理员特权功能
        const adminElements = document.querySelectorAll('.admin-only');
        // VIP特权功能
        const vipElements = document.querySelectorAll('.vip-only');
        // 普通用户功能
        const normalUserElements = document.querySelectorAll('.normal-user-only');
        
        if (role === '2') { // 管理员
            // 显示管理员元素
            adminElements.forEach(el => el.style.display = 'block');
            // 显示VIP元素（管理员拥有所有权限）
            vipElements.forEach(el => el.style.display = 'block');
            // 显示普通用户元素
            normalUserElements.forEach(el => el.style.display = 'block');
        } else if (role === '1') { // VIP用户
            // 隐藏管理员元素
            adminElements.forEach(el => el.style.display = 'none');
            // 显示VIP元素
            vipElements.forEach(el => el.style.display = 'block');
            // 显示普通用户元素
            normalUserElements.forEach(el => el.style.display = 'block');
        } else { // 普通用户 (role === '0')
            // 隐藏管理员元素
            adminElements.forEach(el => el.style.display = 'none');
            // 隐藏VIP元素
            vipElements.forEach(el => el.style.display = 'none');
            // 显示普通用户元素
            normalUserElements.forEach(el => el.style.display = 'block');
        }
    }
    
    // 退出登录
    function logout() {
        axios.post('/api/user/logout')
            .then(function() {
                clearUserData();
                window.location.href = '/static/auth.html';
            })
            .catch(function(error) {
                console.error('Logout error:', error);
                clearUserData();
                window.location.href = '/static/auth.html';
            });
    }
    
    // 清除用户数据
    function clearUserData() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userId');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userPhone');
        localStorage.removeItem('userRole');
    }
    
    // 公开API
    window.AuthHelper = {
        init: init,
        setupAxiosInterceptors: setupAxiosInterceptors,
        setupNavigationLinks: setupNavigationLinks,
        validateLoginState: validateLoginState,
        logout: logout,
        clearUserData: clearUserData,
        updateUIByRole: updateUIByRole
    };
    
    // 页面加载完成后自动初始化
    document.addEventListener('DOMContentLoaded', init);
})(); 