# Use Case Specification by Role

## 1. Scope and objective

Tai lieu nay dac ta use case theo 4 role trong he thong:
- `Admin`
- `Manager`
- `Buyer`
- `Consigner`

No duoc thiet ke de lam dau vao cho API spec, user story, test case, va phan quyen.

Can cu domain model: `FCS_BE/docs/DB_DESIGN.md`.

---

## 2. Standard use case template

Moi use case trong tai lieu deu theo format:

- **Use case ID**
- **Actor**
- **Goal**
- **Preconditions**
- **Trigger**
- **Main flow**
- **Alternative flow**
- **Exception flow**
- **Business rules**
- **Input/Output data**
- **Postconditions**

---

## 3. Admin use cases

### UC-ADM-01 Manage Roles and Permissions
- **Actor**: Admin
- **Goal**: Tao/sua role va gan permission cho role.
- **Preconditions**: Admin dang dang nhap, co quyen `iam.role.manage`.
- **Trigger**: Admin chon man hinh Role & Permission.
- **Main flow**:
  1. He thong hien danh sach role va permission.
  2. Admin tao moi hoac chon role can chinh sua.
  3. Admin cap nhat ten/mo ta role.
  4. Admin gan/go permission vao role.
  5. He thong luu thay doi vao `roles`, `role_permissions`.
- **Alternative flow**:
  - Role da ton tai -> chuyen sang cap nhat.
- **Exception flow**:
  - Permission code khong hop le -> tu choi luu, hien loi validation.
- **Business rules**:
  - Role name unique.
  - Permission code phai ton tai va `is_deleted=false`.
- **Input/Output data**:
  - Input: role info + danh sach permission IDs.
  - Output: role version moi + mapping permission.
- **Postconditions**: Role/permission mapping duoc cap nhat va co audit log.

### UC-ADM-02 Lock or Unlock User Account
- **Actor**: Admin
- **Goal**: Khoa/mo tai khoan user theo chinh sach an ninh.
- **Preconditions**: Admin co quyen `iam.user.lock`.
- **Trigger**: Admin thao tac tren man hinh user.
- **Main flow**:
  1. Tim user theo username/email.
  2. Chon hanh dong lock hoac unlock.
  3. He thong cap nhat `users.status`.
  4. Tao `activity_logs`.
- **Alternative flow**:
  - User dang `DELETED` -> chi duoc xem, khong unlock.
- **Exception flow**:
  - Admin tu khoa chinh minh -> chan thao tac.
- **Business rules**:
  - Status hop le: `ACTIVE` <-> `LOCKED`.
- **Input/Output data**:
  - Input: userId, action, reason.
  - Output: trang thai user moi.
- **Postconditions**: User bi khoa/mo va duoc trace.

### UC-ADM-03 Manage Catalog and System Settings
- **Actor**: Admin
- **Goal**: Quan tri `categories`, `brands`, `system_settings`.
- **Preconditions**: Co quyen catalog/system config.
- **Trigger**: Admin mo module Catalog/Settings.
- **Main flow**:
  1. Tao/sua/xoa mem category, brand.
  2. Cap nhat cau hinh he thong (`setting_key`, `setting_value`).
  3. He thong validate unique va quan he cha-con.
  4. He thong luu va ghi log.
- **Alternative flow**:
  - Xoa category co product dang gan -> tu choi, yeu cau go mapping truoc.
- **Exception flow**:
  - Trung `slug` hoac `key` -> bao loi conflict.
- **Business rules**:
  - `categories.slug` unique.
  - `system_settings.key` unique.
- **Input/Output data**:
  - Input: category/brand/setting payload.
  - Output: ban ghi moi da persist.
- **Postconditions**: Danh muc va config duoc dong bo.

### UC-ADM-04 Review Audit Logs and System Notifications
- **Actor**: Admin
- **Goal**: Giam sat thao tac he thong va gui thong bao he thong.
- **Preconditions**: Co quyen audit read va notification publish.
- **Trigger**: Admin mo dashboard van hanh.
- **Main flow**:
  1. Loc `activity_logs` theo user/action/entity/time.
  2. Xem chi tiet old/new values.
  3. Tao thong bao he thong.
  4. He thong tao `notifications` + `user_notifications`.
- **Alternative flow**:
  - Gui cho nhom nguoi dung thay vi all users.
- **Exception flow**:
  - Du lieu log qua lon -> phan trang bat buoc.
- **Business rules**:
  - Activity log immutable.
- **Input/Output data**:
  - Input: filter criteria, notification content.
  - Output: danh sach log, notification ids.
- **Postconditions**: Co ban ghi thong bao va tracking da doc.

---

## 4. Manager use cases

### UC-MGR-01 Process Consignment Request
- **Actor**: Manager
- **Goal**: Duyet request ky gui.
- **Preconditions**: Request o trang thai `SUBMITTED`/`UNDER_REVIEW`.
- **Trigger**: Manager mo queue consignment.
- **Main flow**:
  1. Xem `consignment_requests` + `consignment_items`.
  2. Danh gia tinh trang item.
  3. Cap nhat status item va request.
  4. Tao `consignment_status_history`.
- **Alternative flow**:
  - Can bo sung thong tin -> tra ve `UNDER_REVIEW` + note.
- **Exception flow**:
  - Item vi pham policy -> `REJECTED` + `rejectionReason`.
- **Business rules**:
  - 1 request chi co 1 item va 1 contract.
  - Chuyen trang thai theo state machine da quy dinh.
- **Input/Output data**:
  - Input: decision, note, rejectionReason.
  - Output: status moi cua request/item.
- **Postconditions**: Ho so ky gui duoc cap nhat va luu lich su.

### UC-MGR-02 Finalize Consignment Contract
- **Actor**: Manager
- **Goal**: Chot hop dong ky gui.
- **Preconditions**: Request da du dieu kien approved.
- **Trigger**: Manager chon "Create/Update Contract".
- **Main flow**:
  1. Nhap `commissionRate`, `agreedPrice`, `validUntil`.
  2. He thong tao/cap nhat `consignment_contracts`.
  3. Ky so/ghi nhan `signedAt` khi chap nhan.
- **Alternative flow**:
  - Chua ky ngay -> de `DRAFT`.
- **Exception flow**:
  - Commission vuot policy -> bao loi, khong cho save.
- **Business rules**:
  - 1 request co toi da 1 contract.
- **Input/Output data**:
  - Input: contract payload.
  - Output: contract status.
- **Postconditions**: Contract hop le duoc luu.

### UC-MGR-03 Publish Product and Manage Inventory Lifecycle
- **Actor**: Manager
- **Goal**: Dua item da duyet thanh san pham ban duoc va quan ly kho.
- **Preconditions**: Item `ACCEPTED` va contract hop le.
- **Trigger**: Manager chon action "Create Product".
- **Main flow**:
  1. Tao `products` tu item.
  2. Gan brand/category qua `product_categories`.
  3. Upload media vao `media_assets`.
  4. Set status product (`READY_TO_LIST` -> `SELLING`).
  5. Tao `warehouse_logs`.
- **Alternative flow**:
  - Tam giu hang -> set `HOLD`.
- **Exception flow**:
  - SKU trung -> khong tao product, yeu cau SKU moi.
- **Business rules**:
  - 1 item chi sinh 1 product.
  - Product phai co it nhat 1 media chinh.
- **Input/Output data**:
  - Input: SKU, gia ban, media list, category list.
  - Output: productId + status.
- **Postconditions**: Product san sang ban hoac dang ban.

### UC-MGR-04 Handle Order Exception and Voucher Operation
- **Actor**: Manager
- **Goal**: Xu ly don bat thuong va van hanh voucher.
- **Preconditions**: Don phat sinh su co hoac voucher can can thiep.
- **Trigger**: Dashboard exception canh bao.
- **Main flow**:
  1. Mo chi tiet order.
  2. Dieu chinh status theo quyen.
  3. Cap nhat voucher `ACTIVE/INACTIVE`.
  4. Ghi `order_status_history` va `activity_logs`.
- **Alternative flow**:
  - Hoan tien thu cong -> tao wallet transaction phu hop.
- **Exception flow**:
  - Don da `COMPLETED` -> chan huy don.
- **Business rules**:
  - Khong rollback status trai chieu quy trinh fulfillment.
- **Input/Output data**:
  - Input: orderId/voucherId + action.
  - Output: status moi.
- **Postconditions**: Don/voucher ve trang thai hop le.

---

## 5. Buyer use cases

### UC-BUY-01 Browse and Search Products
- **Actor**: Buyer
- **Goal**: Tim va xem san pham phu hop.
- **Preconditions**: Product dang `SELLING`.
- **Trigger**: Buyer vao trang catalog.
- **Main flow**:
  1. Tim theo tu khoa/category/brand/price.
  2. Xem chi tiet product + media.
  3. Chon san pham muon mua.
- **Alternative flow**:
  - Khong co ket qua -> goi y danh muc lien quan.
- **Exception flow**:
  - Product vua `SOLD/HOLD` -> disabled add-to-cart.
- **Business rules**:
  - Chi hien product `isDeleted=false` va status hop le.
- **Input/Output data**:
  - Input: filter params.
  - Output: danh sach product.
- **Postconditions**: Buyer chon duoc product quan tam.

### UC-BUY-02 Manage Cart
- **Actor**: Buyer
- **Goal**: Them/xoa product khoi gio hang.
- **Preconditions**: Co cart cua user hoac guest session.
- **Trigger**: Buyer click add/remove cart.
- **Main flow**:
  1. Them product vao `cart_items`.
  2. Kiem tra ton tai product trong cart.
  3. Hien thi tong gia tri tam tinh.
- **Alternative flow**:
  - Guest cart merge vao user cart sau login.
- **Exception flow**:
  - Product khong con ban duoc -> tu dong remove khoi cart.
- **Business rules**:
  - Moi product xuat hien toi da 1 lan trong 1 cart.
- **Input/Output data**:
  - Input: cart action + productId.
  - Output: cart state moi.
- **Postconditions**: Cart duoc dong bo.

### UC-BUY-03 Checkout and Place Order
- **Actor**: Buyer
- **Goal**: Dat hang thanh cong.
- **Preconditions**: Cart hop le, co dia chi giao hang.
- **Trigger**: Buyer bam "Place order".
- **Main flow**:
  1. Chon dia chi, phuong thuc thanh toan, voucher.
  2. He thong tinh gia, tao `orders` + `order_items`.
  3. Snapshot thong tin san pham va dia chi.
  4. Dat status ban dau `PENDING_PAYMENT`/`PAID`.
- **Alternative flow**:
  - Voucher khong hop le -> checkout khong ap dung giam gia.
- **Exception flow**:
  - Product da duoc dat truoc -> checkout fail tung item.
- **Business rules**:
  - `orderCode` unique.
  - `OrderItem.priceAtPurchase` bat buoc snapshot.
- **Input/Output data**:
  - Input: cart, shippingAddress, paymentMethod, voucherCode.
  - Output: order summary + orderCode.
- **Postconditions**: Don duoc tao va truong thai ban dau hop le.

### UC-BUY-04 Track Order and Read Notifications
- **Actor**: Buyer
- **Goal**: Theo doi tien trinh don hang.
- **Preconditions**: Buyer co don hang.
- **Trigger**: Buyer mo Order History.
- **Main flow**:
  1. Xem danh sach don.
  2. Mo chi tiet status timeline.
  3. Doc thong bao lien quan don.
  4. He thong cap nhat `user_notifications.isRead`.
- **Alternative flow**:
  - Loc theo khoang thoi gian/status.
- **Exception flow**:
  - Don bi huy -> hien ly do huy.
- **Business rules**:
  - Buyer chi duoc xem don cua chinh minh.
- **Input/Output data**:
  - Input: buyerId + filter.
  - Output: orders + notifications.
- **Postconditions**: Buyer nam duoc trang thai moi nhat.

---

## 6. Consigner use cases

### UC-CON-01 Create Consignment Request
- **Actor**: Consigner
- **Goal**: Tao yeu cau ky gui moi.
- **Preconditions**: Consigner da KYC/active.
- **Trigger**: Chon "Create consignment request".
- **Main flow**:
  1. Nhap thong tin item (`suggestedName`, `suggestedPrice`, `conditionNote`).
  2. Upload media item.
  3. Tao `consignment_requests` + `consignment_items`.
  4. Dat status `SUBMITTED`.
- **Alternative flow**:
  - Luu nhap dang draf -> status `DRAFT`.
- **Exception flow**:
  - Thieu media toi thieu -> khong cho submit.
- **Business rules**:
  - Moi request chi co 1 item.
- **Input/Output data**:
  - Input: item payload + media.
  - Output: request code + status.
- **Postconditions**: Request vao hang doi duyet.

### UC-CON-02 Track Inspection and Contract
- **Actor**: Consigner
- **Goal**: Theo doi ket qua kiem dinh va hop dong.
- **Preconditions**: Da co consignment request.
- **Trigger**: Mo trang "My consignments".
- **Main flow**:
  1. Xem status request/item.
  2. Xem ket qua accepted/rejected.
  3. Xem contract va dieu khoan chia doanh thu.
- **Alternative flow**:
  - Neu reject -> xem rejectionReason va huong dan xu ly.
- **Exception flow**:
  - Contract het han -> thong bao cap nhat.
- **Business rules**:
  - Lich su status duoc doc tu bang history, khong sua.
- **Input/Output data**:
  - Input: requestId.
  - Output: timeline + contract data.
- **Postconditions**: Consigner nam ro tinh trang ky gui.

### UC-CON-03 Monitor Revenue in Wallet
- **Actor**: Consigner
- **Goal**: Theo doi so du va lich su bien dong vi.
- **Preconditions**: Co wallet.
- **Trigger**: Mo module Wallet.
- **Main flow**:
  1. Xem `balance`, `availableBalance`.
  2. Xem danh sach `wallet_transactions`.
  3. Loc theo loai giao dich va thoi gian.
- **Alternative flow**:
  - Export lich su giao dich.
- **Exception flow**:
  - Wallet chua khoi tao -> yeu cau contact support.
- **Business rules**:
  - Ledger transaction immutable.
- **Input/Output data**:
  - Input: wallet filter.
  - Output: balance + transaction list.
- **Postconditions**: Consigner doi soat doanh thu.

### UC-CON-04 Create Withdrawal Request and Track Payout
- **Actor**: Consigner
- **Goal**: Gui yeu cau rut tien va theo doi xu ly.
- **Preconditions**: `availableBalance` du.
- **Trigger**: Chon "Withdraw".
- **Main flow**:
  1. Nhap so tien rut va xac nhan bank info.
  2. He thong tao `withdrawal_requests` status `PENDING`.
  3. Manager xu ly thu cong.
  4. Consigner theo doi status den `PAID`.
- **Alternative flow**:
  - Cap nhat bank snapshot truoc khi submit.
- **Exception flow**:
  - So tien vuot available balance -> reject ngay tai validation.
- **Business rules**:
  - Xu ly payout la thu cong, he thong chi track trang thai.
- **Input/Output data**:
  - Input: amount + bank snapshot.
  - Output: requestCode + status timeline.
- **Postconditions**: Co withdrawal ticket hop le de doi soat.

---

## 7. Cross-role consistency matrix

| Capability | Admin | Manager | Buyer | Consigner |
|---|---|---|---|---|
| IAM role/permission | Full | No | No | No |
| Catalog/system settings | Full | Limited (operational) | Read only | Read only |
| Consignment approval | Oversight | Full operation | No | Submit/track own |
| Product lifecycle | Oversight | Full operation | View/purchase | View own linked |
| Order exception | Oversight | Full operation | Own orders only | No |
| Wallet/withdrawal approval | Oversight | Process/approve | No | Own wallet/request |
| Audit logs | Full | Partial | No | No |

---

## 8. Notes for next phase

- Moi use case co the tach thanh user story theo format: "As a <role>, I want <goal>, so that <value>".
- Moi business rule nen map thanh test case va validation rule o API layer.
- Cac transition status nen dua vao workflow guard trong service layer va ghi history bat buoc.
