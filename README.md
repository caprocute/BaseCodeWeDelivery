# BaseCodeWeDelivery
Ứng dụng kết nối tài xế có khả năng vận chuyển hàng hóa với khách hàng có nhu cầu giao hàng.
## Các chức năng chính 
  ```
  1 - Đăng nhập
  2 - Quản lý thông tin cá nhân 
  3 - Gọi xe
  4 - Nhận cuốc gọi xe
  5 - Lịch sử sử dụng
  6 - Tài xế ưa thích
  7 - Hòm thư
  ```
## Cài đặt
### Tạo CSDL Firebase Database
  ```
  1 - Vào địa chỉ https://console.firebase.google.com/u/0/?pli=1 đăng nhập bằng tài khoản Google và chọn tạo dự án mới.
  2 - Trên Android Studio IDE, chọn Tools -> Firebase
  3 - Trên cửa sổ Assistant, kích hoạt chức năng Realtime Database và kết nối với dựu án đã tạo ở b1
  ```
### Kích hoạt Aunthencation và Storage
  ```
  1 - Tại cửa sổ Assistant, lần lượt kích hoạt các chức năng Aunthencation và Storage
  2 - Vào địa chỉ https://console.firebase.google.com/u/0/?pli=1, chọn dự án của bạn, tại cửa sổ Develop, chọn Aunthencation -> Tab Sign in Method -> kích hoạt Phone, Email,Google và Anonymous
  ```
### Kích hoạt GoogleMap Api
 ```
 1 - Vào địa chỉ https://console.cloud.google.com/
 2 - TÌm và kích hoạt các api sau:
    Directions API 	
    Geocoding API 
    Maps SDK for Android 	
    Places SDK for Android 
 ```
 Ghi chú: Maps SDK for Android yêu cầu cung cấp key, tham khảo https://developers.google.com/maps/documentation/android-sdk/start để thực hiện
