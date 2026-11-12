"""Kiểm thử qua HTTP; chỉ dùng tài khoản và dữ liệu DEMO. Không cần pip.
Chạy: python tools/http-test6.py http://localhost:8080/restman6
"""
import http.cookiejar
import re
import sys
import urllib.error
import urllib.parse
import urllib.request

BASE = (sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8080/restman6').rstrip('/')
COUNT = 0

def check(condition, name):
    global COUNT
    if not condition:
        raise AssertionError('FAIL: ' + name)
    COUNT += 1
    print('PASS:', name)

class Browser:
    def __init__(self):
        self.jar = http.cookiejar.CookieJar()
        self.client = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(self.jar))

    def request(self, path, data=None):
        payload = None if data is None else urllib.parse.urlencode(data).encode('utf-8')
        try:
            response = self.client.open(BASE + path, payload, timeout=30)
        except urllib.error.HTTPError as e:
            response = e
        return response.status, response.read().decode('utf-8'), response.headers

    def login(self, username, password):
        _, page, _ = self.request('/login')
        token = csrf(page)
        old = next((c.value for c in self.jar if c.name == 'JSESSIONID'), None)
        status, page, _ = self.request('/login', {'username': username, 'password': password, 'csrf': token})
        new = next((c.value for c in self.jar if c.name == 'JSESSIONID'), None)
        return status, page, old, new

def csrf(page):
    match = re.search(r'name="csrf" value="([^"]+)"', page)
    if not match:
        raise AssertionError('Không có token CSRF trên trang')
    return match.group(1)

def query(path, **data):
    return path + '?' + urllib.parse.urlencode(data)

b = Browser()
s, page, _ = b.request('/customer/search')
check(s == 200 and '<h1>Đăng nhập</h1>' in page, 'anonymous redirected to login')
s, _, _ = b.request('/login', {'username':'manager6', 'password':'Manager@123'})
check(s == 403, 'login rejects missing CSRF')
s, page, _, _ = b.login('manager6', 'wrong')
check(s == 401 and 'không đúng' in page, 'wrong password')
s, page, _, _ = b.login('locked6', 'Locked@123')
check(s == 401, 'locked account')
s, page, old, new = b.login('customer6', 'Customer@123')
check(s == 200 and 'Trang chủ khách hàng' in page, 'customer login and redirect')
check(old != new, 'session rotated after login')
s, page, headers = b.request('/customer/search')
check(s == 200 and 'Tên món ăn' in page, 'empty search form')
check(headers.get('Cache-Control') == 'no-store', 'authenticated page not cached')
check("frame-ancestors 'none'" in headers.get('Content-Security-Policy',''), 'security header')
s, page, _ = b.request(query('/customer/search', keyword='  cƠm  '))
check(s == 200 and 'Cơm gà' in page and '65.000 đ' in page, 'search reaches JDBC')
check('id=1&amp;keyword=' in page, 'detail link retains keyword')
for keyword in ['', 'a'*151]:
    s, page, _ = b.request(query('/customer/search', keyword=keyword))
    check(s == 400 and '1 đến 150' in page, 'invalid keyword length ' + str(len(keyword)))
for keyword in ['khôngcómónnày', '%', '_', "' OR 1=1 --"]:
    s, page, _ = b.request(query('/customer/search', keyword=keyword))
    check(s == 200 and 'Không tìm thấy' in page, 'no result for ' + keyword)
s, page, _ = b.request(query('/customer/search', keyword='"><script>alert(1)</script>'))
check(s == 200 and '<script>' not in page and '&lt;script&gt;' in page, 'escape HTML injection')
s, page, _ = b.request(query('/customer/food', id=1, keyword='Cơm'))
check(s == 200 and 'Chi tiết món ăn' in page and '65.000 đ' in page, 'food detail')
check('/customer/search?keyword=C%C6%A1m' in page, 'back link retains search')
s, _, _ = b.request(query('/customer/food', id=999999))
check(s == 404, 'food not found')
s, _, _ = b.request(query('/customer/food', id='abc'))
check(s == 400, 'invalid food id')
s, _, _ = b.request('/manager/statistics')
check(s == 403, 'customer blocked from manager')
s, _, _ = b.request('/logout')
check(s == 405, 'logout requires POST')
s, _, _ = b.request('/logout', {'csrf':'wrong'})
check(s == 403, 'logout checks CSRF')
_, page, _ = b.request('/customer/home')
s, page, _ = b.request('/logout', {'csrf':csrf(page)})
check(s == 200 and '<h1>Đăng nhập</h1>' in page, 'logout success')
s, page, _ = b.request('/customer/home')
check(s == 200 and '<h1>Đăng nhập</h1>' in page, 'session invalidated')

s, page, _, _ = b.login('manager6', 'Manager@123')
check(s == 200 and 'Trang chủ quản lí' in page, 'manager login')
s, _, _ = b.request('/customer/search')
check(s == 403, 'manager blocked from customer route')
s, page, _ = b.request('/manager/reports')
check(s == 200 and 'Thống kê món ăn theo doanh thu' in page and 'chưa cài đặt' in page, 'report selection')
s, page, _ = b.request('/manager/statistics')
check(s == 200 and '2026-10-01' in page, 'statistics input form')
period = dict(start='2026-10-01', end='2026-10-31')
s, page, _ = b.request(query('/manager/statistics', **period))
check(s == 200 and all(x in page for x in ['245.000 đ','170.000 đ','90.000 đ','505.000 đ']), 'report amounts 505000')
s, page, _ = b.request(query('/manager/history', foodId=1, **period))
check(s == 200 and all(x in page for x in ['1001','1003','1005','245.000 đ']), 'history three occurrences')
check('1007' not in page and '1008' not in page and '1009' not in page, 'exclude unpaid and out of range')
params = dict(orderId=101, detailId=1001, foodId=1, **period)
s, page, _ = b.request(query('/manager/invoice', **params))
check(s == 200 and 'Hóa đơn #201' in page and '210.000 đ' in page, 'invoice total')
check('Nước cam' in page and '1003' in page, 'invoice includes all foods and repeated occurrence')
check('<tr class="selected"><td>1001</td>' in page, 'highlight selected occurrence')
check('foodId=1&amp;start=2026-10-01&amp;end=2026-10-31' in page, 'history back link retains filter')
for change in [dict(detailId=1004), dict(foodId=3), dict(start='2026-11-01',end='2026-11-30')]:
    bad = dict(params, **change)
    s, _, _ = b.request(query('/manager/invoice', **bad))
    check(s == 404, 'reject wrong invoice context ' + str(change))
s, _, _ = b.request(query('/manager/invoice', orderId=103, detailId=1007, foodId=1, **period))
check(s == 404, 'reject unpaid invoice in report')
s, _, _ = b.request(query('/manager/invoice', orderId=107, detailId=1011, foodId=3, **period))
check(s == 404, 'missing invoice')
for dates in [dict(start='2026-10-31',end='2026-10-01'),dict(start='2026-02-30',end='2026-03-01'),dict(start='2026-10-01')]:
    s, _, _ = b.request(query('/manager/statistics', **dates))
    check(s == 400, 'invalid period ' + str(dates))
s, page, _ = b.request(query('/manager/statistics', start='2025-01-01',end='2025-01-31'))
check(s == 200 and 'Không có dữ liệu' in page and '0 đ' in page, 'empty report')
s, page, _ = b.request(query('/manager/statistics', start='2026-11-01',end='2026-11-01'))
check(s == 200 and '65.000 đ' in page, 'include payment at start of November')
s, page, _ = b.request(query('/manager/history', foodId=4, **period))
check(s == 200 and 'Không có lần gọi' in page, 'food without history')
s, _, _ = b.request('/manager/statistics', period)
check(s == 405, 'read-only page rejects POST')
print('TOTAL:', COUNT, 'HTTP checks passed')
print('BASE:', BASE)
