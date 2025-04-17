# ecom_platform

## 需要完成部分：

- 用户管理功能（注册、登陆、个人信息，**普通用户角色部分**）

  （1）注册与登录：支持邮箱/手机号注册，验证码校验；支持用户名+密码登录，集成第三方登录（微信、支付宝等）；忘记密码时可通过邮箱/手机号找回。

  （2）个人信息管理√：查看和修改个人信息（如姓名、性别、年龄、联系方式、密码等）；

  绑定/解绑邮箱、手机号、第三方账号；可以进行短信验证。

  （3）地址管理√：添加、编辑、删除收货地址，并设置默认地址；可以集成地图API自动匹配省市区、街道信息。

  （4）消费统计：可以查看某个时间段内的消费统计（如每日/每周/每月/自定义周期查看消费记录）；柱状图或折线图展示消费趋势（如总花销、订单量）；按商品类别（如食品、服饰显示消费占比）；支持导出Excel/PDF格式的消费明细。

> **注册与登录**
>
> -  支持邮箱、手机号注册，验证码校验
> - 用户名+密码登录，集成微信、支付宝第三方登录
> -  通过邮箱/手机号找回密码
>
> **个人信息管理**
>
> - 查看/修改个人信息（姓名、性别、年龄、联系方式、密码）
>   - 查看个人信息：除密码外都可以看 √
>   - 修改个人信息：除密码外都修改√
>   - 密码修改（重置密码接口）：
>     - 根据邮箱获取验证码：√
>     - 根据验证码以及对应新密码进行密码修改：√
> - 绑定/解绑邮箱、手机号、第三方账号，支持短信验证
>   - 绑定/解绑邮箱：
>   - 绑定/解绑手机号：
>   - 绑定/解绑第三方账号：
>
> **地址管理√** 
>
> - 添加、编辑、删除收货地址 
>
>   - 添加： √
>
>     ```json
>     {
>       "recipientName": "张三",
>       "phone": "13812345678",
>       "addressLine1": "阳光小区12号楼",
>       "addressLine2": "3单元401室",
>       "city": "北京市",
>       "state": "海淀区",
>       "postalCode": "100081",
>       "country": "中国",
>       "isDefault": true
>     }
>     ```
>
>   - 编辑： √
>   - 删除收货 √
>
> - 设置默认地址 √
>
> **消费统计**
>
> - 按时间段统计消费记录，支持每日/每周/每月/自定义周期
>
> - 图表展示（柱状图、折线图）
> - 按商品类别分析消费占比

- 管理员用户管理（敏感操作部分先不做，**管理员角色部分**）

  （1）用户列表：查看用户ID、注册时间、消费总额等。

  （2）权限管控：分配用户角色（普通用户，VIP用户），禁用违规账号。


> - 用户列表
>   - 全列表分页查询 √
>   - ID √
>   - 邮箱 √
>   - 注册时间 √
>   - 消费总额 √
> - 角色管理（普通用户/VIP用户）√
> - 账号禁用 √

## 表结构说明

**3.1.1** **用户基本信息表**

```
users 
(user_id（整数）：用户ID（主键）
username（字符串）：用户名
password（字符串）：用户密码（加密存储）
email（字符串）：用户电子邮箱
phone（字符串）：用户手机号码
age（整数）：用户年龄
role（整数）：0-普通用户，1-VIP用户，2-管理员
gender（字符串）：用户性别
is_vip（布尔）：用户是否为会员
create_time（字符串）：用户创建时间
update_time（字符串）：用户信息更新时间)
```

**3.1.2** **商品分类表**

```
categories 
(category_id（字符串）：类别ID（主键）
name（字符串）：类别名称
parent_id（字符串）：父分类ID)
```

**3.1.3** **商品表**

```
products
 (product_id（字符串）：商品ID（主键）
name（字符串）：商品名
description（字符串）：商品描述
price（浮点数）：商品价格
stock（整数）：商品库存
category_id（字符串）：商品所属类别ID
create_time（字符串）：商品创建时间
update_time（字符串）)：商品信息更新时间
```

**3.1.4** **地址表**

```
addresses 
(address_id（字符串）：地址ID（主键）
user_id（字符串）：收货人ID/用户ID
recipient_name（字符串）：收货人姓名
phone（字符串）：收货人手机号码
address_line1, address_line2,（字符串）：两个地址行
city（字符串）：收货人城市
state（字符串）：收货人省份/州
postal_code（字符串）: 收货人所在地邮政编码
country（字符串）: 国家
is_default（字符串）: 是否为默认地址
create_time（字符串）: 地址创建时间
)
```

**3.1.5** **订单表**

```
orders 
(order_id（字符串）：订单ID（主键）
user（字符串）：订购用户
total_amount（浮点数）：订单总金额
create_time（字符串）：订单创建时间
update_time（字符串）：订单更新时间
)
order_items (
order_item_id（字符串）：订单物品ID（主键）
order_id（字符串）：订单ID
product_id（字符串）：商品ID，关联商品基本信息
quantity（整数）：订购商品数量
price（浮点数）：商品单价
create_time（字符串）：订单明细创建时间)
3.1.6 购物车表
carts 
(cart_id（字符串）：购物车ID（主键）
user_id（字符串）：用户ID
product_id（字符串）：商品ID
quantity（整数）：商品数量
create_time（字符串）：购物车创建时间
)
```

**3.1.7** **优惠券表**

```
coupons 
(coupon_id（字符串）：优惠券ID（主键）
type（字符串）：优惠券类型（包括满减、折扣、固定金额等类型）
discount_value（字符串）：折扣值（包括满减金额、折扣比例等类型）
min_order_amount（浮点数）：最低订单金额要求
start_time（字符串）：优惠券生效时间
end_time（字符串）：优惠券失效时间
status（字符串）：优惠券状态（包含未使用、已使用、已过期三个状态）
create_time（字符串）：优惠券创建时间
update_time（字符串）：优惠券更新时间
)
```

**3.1.8** **评价表**

```
reviews
 (review_id（字符串）：评价ID（主键）
product_id（字符串）：商品ID，关联商品基本信息。
user_id（字符串）：用户ID，关联用户基本信息。
order_id（字符串）：订单ID，关联订单基本信息（新增字段，确保用户购买后才能评价）。
rating（整数）：评分（提供五个星级，此处记录星级数）。
comment（字符串）：评价内容。
create_time（字符串）：评价创建时间。
update_time（字符串）：评价更新时间。
)
```

## swagger

用于测试验收

地址：http://localhost:8088/swagger-ui/index.html

- 右上角选择接口分组
- 下方选择接口调用
  - 填写相应参数
  - 点击Execute按钮，执行接口

## 获取地区api

- 获取省名：`https://restapi.amap.com/v3/config/district?subdistrict=1&key=1e5fbfe5f8be999db82f1ca1fbd3e84a`
- 获取市名：`https://restapi.amap.com/v3/config/district?keywords=省名（例如陕西省）&subdistrict=1&key=1e5fbfe5f8be999db82f1ca1fbd3e84a`
- 获取区名：`https://restapi.amap.com/v3/config/district?keywords=区名（例如西安市）&subdistrict=1&key=1e5fbfe5f8be999db82f1ca1fbd3e84a`

> 前端调用，以表单形式调用接口即可