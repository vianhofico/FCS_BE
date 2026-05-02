# Context

Mục tiêu là triển khai **toàn bộ frontend end-to-end** cho FCS theo vai trò (Buyer, Seller, Manager, Admin), bám sát spec + thiết kế Figma, đồng thời tuân thủ rules/skills sẵn có của repo.

Hiện trạng FE mới ở mức scaffold: router + layout + placeholder pages, API layer còn là stub và có lệch contract/endpoint so với backend thực tế. Vì vậy cần đi theo hướng: **chuẩn hoá nền tảng trước, rồi rollout theo vai trò Buyer → Seller → Manager → Admin**, sau đó hoàn thiện các tích hợp nâng cao (payment, shipping, reporting, realtime hoàn chỉnh).

---

## Recommended implementation plan

## Phase 0 — Discovery lock + screen inventory from Figma

**Objective**
- Chốt danh sách màn hình/component theo Figma và map 1-1 với screen IDs trong specs.

**Deliverables**
- Screen inventory chuẩn cho Buyer/Seller/Manager/Admin (MVP + advanced).
- Mapping: `Figma node -> FE route -> API spec section -> module owner`.
- Danh sách reusable UI patterns (table/filter/form/detail/timeline/status actions).

**Critical files to modify**
- `docs/` (thêm/chuẩn hoá screen inventory & mapping table).
- `FCS_FE/docs/fe-be-alignment.md`.

**Dependencies**
- Quyền truy cập Figma file thiết kế.

**Exit criteria**
- 100% screen trong các file spec được gắn route và owner module.
- Không còn ambiguity về màn nào thuộc role nào.

---

## Phase 1 — Foundation & FE-BE contract hardening (must-have)

**Objective**
- Cố định nền tảng kỹ thuật để tránh build sai hàng loạt ở các phase role.

**Deliverables**
1. Chuẩn hoá API contract:
   - `ApiResponse<T>` + error envelope + pagination `data.content/page/size/totalElements/totalPages`.
2. Chuẩn hoá endpoint roots theo backend thực tế (`/products`, `/consignments`, `/orders`, `/notifications`, `/audit/activity-logs`, ...).
3. Auth/session framework:
   - lưu token, attach `Authorization`, refresh flow, logout flow.
4. Error handling thống nhất cho `400/401/403/404/409`.
5. Route guard nền tảng (authenticated + role guard).
6. Shared primitives:
   - `PageState` (Loading/Empty/Error/Data), table query state trên URL, status badge map theo enums.

**Critical files to modify**
- `FCS_FE/src/shared/contracts/apiContract.ts`
- `FCS_FE/src/shared/api/endpoints.ts`
- `FCS_FE/src/shared/api/http.ts`
- `FCS_FE/src/app/router/router.tsx`
- `FCS_FE/src/app/router/routeManifest.tsx`
- `FCS_FE/src/app/layout/AppLayout.tsx`
- `FCS_FE/src/app/config/env.ts`
- `FCS_FE/.env.example`

**Reuse existing assets**
- Env loader: `FCS_FE/src/app/config/env.ts`
- Centralized HTTP client: `FCS_FE/src/shared/api/http.ts`
- Module-route alignment metadata: `FCS_FE/src/app/router/routeManifest.tsx`
- Placeholder scaffold component: `FCS_FE/src/shared/components/ModulePlaceholderPage.tsx`

**Dependencies**
- FE-BE endpoint confirmation theo controller mappings hiện tại.

**Exit criteria**
- Tất cả module API calls dùng envelope/pagination đúng contract.
- Không còn endpoint path drift ở layer `shared/api` và `modules/*/api`.
- Guard + unauthorized handling hoạt động end-to-end.

---

## Phase 2 — Buyer release (commerce core + full buyer spec)

**Objective**
- Hoàn chỉnh toàn bộ buyer journey từ auth đến mua hàng/hậu mua.

**Deliverables**
1. Auth screens: login/register/forgot/reset/logout.
2. Product browse: listing/filter/sort/detail/media/review summary.
3. Wishlist + review submit flow.
4. Address book.
5. Cart + checkout + voucher validate + create order.
6. Order history/detail + cancel + return request.
7. Profile + change password.
8. Buyer wallet + transactions + notifications.
9. Buyer chat (REST + STOMP) theo spec.

**Critical files/modules to modify**
- `FCS_FE/src/modules/iam/**`
- `FCS_FE/src/modules/product/**`
- `FCS_FE/src/modules/order/**`
- `FCS_FE/src/modules/financial/**`
- `FCS_FE/src/modules/notification/**`
- `FCS_FE/src/modules/chat/**` (new module, giữ shared ở `src/shared`)
- `FCS_FE/src/modules/wishlist/**` (new)
- `FCS_FE/src/modules/review/**` (new)
- `FCS_FE/src/modules/return_request/**` (new)

**Dependencies**
- Phase 1 contract/auth/guard hoàn tất.

**Exit criteria**
- Golden path Buyer chạy trọn vẹn: đăng nhập -> duyệt SP -> giỏ -> checkout -> theo dõi đơn.
- Tất cả list buyer có pagination + URL query state.
- Màn buyer có đủ Loading/Empty/Error/Data.

---

## Phase 3 — Seller release (consignment + seller finance)

**Objective**
- Hoàn chỉnh seller flows: ký gửi, hợp đồng, ví/rút tiền, giao tiếp vận hành.

**Deliverables**
1. Seller dashboard KPI cơ bản.
2. Consignment flow nhiều bước: request -> item -> media -> submit.
3. Consignment list/detail + status timeline + cancel/update note.
4. Contract view + sign.
5. Seller wallet + bank info + transactions.
6. Withdrawal create/list/detail (+ cancel nếu policy mở).
7. Seller notifications + chat.

**Critical files/modules to modify**
- `FCS_FE/src/modules/consignment/**`
- `FCS_FE/src/modules/financial/**`
- `FCS_FE/src/modules/notification/**`
- `FCS_FE/src/modules/chat/**`
- `FCS_FE/src/modules/analytics/**` (new seller panels nếu tách module)

**Dependencies**
- Buyer phase ổn định và shared state/actions đã reusable.

**Exit criteria**
- Golden path Seller chạy trọn: tạo ký gửi -> submit -> xem contract -> ký -> theo dõi ví/rút tiền.
- Status transitions hiển thị đúng enum/state machine.

---

## Phase 4 — Manager release (operations backoffice)

**Objective**
- Cung cấp toàn bộ màn vận hành để xử lý nghiệp vụ hằng ngày.

**Deliverables**
1. Operations dashboard (analytics dashboard/revenue/consignments).
2. Consignment moderation: duyệt request/item/contract.
3. Product backoffice: CRUD + categories + media + warehouse logs.
4. Order management: list/detail/status/tracking.
5. Return/refund processing theo transition rules.
6. Withdrawal approval flow.
7. Voucher + catalog settings + user ops.
8. Audit + notifications + chat vận hành.

**Critical files/modules to modify**
- `FCS_FE/src/modules/analytics/**` (new)
- `FCS_FE/src/modules/consignment/**`
- `FCS_FE/src/modules/product/**`
- `FCS_FE/src/modules/order/**`
- `FCS_FE/src/modules/financial/**`
- `FCS_FE/src/modules/catalog/**`
- `FCS_FE/src/modules/iam/**`
- `FCS_FE/src/modules/audit/**`

**Dependencies**
- Seller phase xong để tái sử dụng status/timeline components.

**Exit criteria**
- Manager xử lý được end-to-end 4 luồng chính: consignment moderation, order ops, return/refund, withdrawal approval.
- Các action quan trọng có confirm modal + error reason rõ.

---

## Phase 5 — Admin release (governance + security ops)

**Objective**
- Hoàn thiện lớp quản trị cấp cao, đặc biệt IAM role/permission.

**Deliverables**
1. Role management (list/create/update/assign permissions/delete).
2. Permission management (list/create/update/delete).
3. User governance (filter/status/assign-remove roles).
4. Token preview/security ops utilities.
5. Admin dashboard & system-level reporting pages.

**Critical files/modules to modify**
- `FCS_FE/src/modules/iam/**`
- `FCS_FE/src/modules/analytics/**`
- `FCS_FE/src/app/router/**` (admin-only guards + menu visibility)

**Dependencies**
- Manager phase hoàn chỉnh.

**Exit criteria**
- Non-admin không truy cập được admin screens (guard FE + 403 handling đúng).
- Role/permission workflows hoàn chỉnh với diff/confirm trước thao tác destructive.

---

## Phase 6 — Cross-role realtime, notification depth, UX polish

**Objective**
- Nâng chất lượng trải nghiệm realtime + đồng bộ trạng thái liên vai trò.

**Deliverables**
- STOMP chat realtime hoàn chỉnh + reconnect strategy + REST fallback.
- Notification center đa role, unread counters, role-specific action links.
- Shared timeline/history widgets cho order/consignment/return/withdrawal.
- Accessibility + responsive polishing theo thiết kế Figma.

**Critical files/modules to modify**
- `FCS_FE/src/modules/chat/**`
- `FCS_FE/src/modules/notification/**`
- `FCS_FE/src/shared/components/**`
- `FCS_FE/src/shared/hooks/**`

**Exit criteria**
- Realtime luồng chat/notification chạy ổn định trong manual stress test.
- Không còn screen placeholder ở các role chính.

---

## Phase 7 — Extended integrations (full spec end-to-end)

**Objective**
- Hoàn thành các hạng mục mở rộng trong FEATURE_SPEC (đỏ/vàng/xanh) cần tích hợp ngoài hoặc BE extension.

**Deliverables**
- Payment gateways (VNPAY/MoMo/COD expansions).
- Shipping integrations + tracking sync.
- Reporting export (PDF/Excel).
- Marketing/CMS-ish panels (banner/flash sale/broadcast) nếu phạm vi giữ nguyên “full spec”.

**Critical files/modules to modify**
- `FCS_FE/src/modules/order/**`
- `FCS_FE/src/modules/financial/**`
- `FCS_FE/src/modules/analytics/**`
- `FCS_FE/src/modules/catalog/**`
- integration-specific shared adapters under `FCS_FE/src/shared/integrations/**`

**Dependencies**
- Có API/backend support hoặc contract extension rõ ràng.

**Exit criteria**
- Các tích hợp ngoài chạy E2E ở môi trường staging với test checklist đầy đủ.

---

## Phase 8 — Hardening, QA/UAT, release readiness

**Objective**
- Chốt chất lượng trước rollout production.

**Deliverables**
- Regression suite theo luồng nghiệp vụ chính (Buyer/Seller/Manager/Admin).
- Error observability, retry policies, fallback states.
- Perf pass cho list pages lớn (audit/orders/products/withdrawals).
- UAT checklist theo role + sign-off.

**Critical files/modules to modify**
- `FCS_FE/src/**` (targeted fixes)
- `docs/` (UAT checklist, runbook)

**Exit criteria**
- Pass toàn bộ quality gates + role-based UAT sign-off.
- Không còn critical contract drift FE-BE.

---

## Phase execution rules (must-follow)

1. Luôn bám rule:
   - `.cursor/rules/fe-backend-module-alignment.mdc`
   - `.cursor/rules/fe-api-response-contract.mdc`
   - `.cursor/rules/fe-env-cors-integration.mdc`
   - `.cursor/rules/rule-precedence.mdc`
2. Dùng skill đúng điểm:
   - `fe-module-alignment-scaffold` khi mở module/screen mới.
   - `fe-be-api-contract-sync` mỗi lần thêm/chỉnh API call.
   - `api-contract-check` khi phát hiện drift cần xác minh lại contract backend.
3. Mọi phase phải hoàn thành 4 UI states (Loading/Empty/Error/Data) cho màn mới.
4. Query state list pages phải giữ trên URL (`page/size/sort/filter`).

---

## Verification strategy (per phase)

1. **Run app + manual flow**
   - `npm run dev` tại `FCS_FE`
   - Kiểm thử golden paths tương ứng phase bằng data thật từ backend.
2. **Static quality gates**
   - `npm run typecheck`
   - `npm run lint`
   - `npm run build`
3. **Contract verification**
   - So endpoint/query/body với controllers BE trước khi merge.
   - Validate error handling cho `400/401/403/404/409`.
4. **Role verification**
   - Test menu visibility + route guards + forbidden flows cho từng role.
5. **Release checkpoint**
   - Mỗi phase có checklist pass/fail riêng, chỉ mở phase kế tiếp khi pass toàn bộ.
