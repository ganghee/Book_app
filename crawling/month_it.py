from bs4 import BeautifulSoup
from date_kind import month_book, driver, it_book
import pymysql.cursors

month_book()
it_book()

req = driver.page_source
soup = BeautifulSoup(req, 'html.parser')

title = soup.find_all('div', class_='title')
author = soup.find_all('span', class_='n1')
link = soup.find_all('div', class_='pic_area')

book_title = [div.find('strong').text for div in title]
book_author = [div.text for div in author]
book_link = [div.find('a')['href'] for div in link]
go = []
for i in book_link:
    go.append("http://digital.kyobobook.co.kr" + i)


conn = pymysql.connect(host='localhost',
                       user='root',
                       password='rootpass',
                       db='bookdb',
                       charset='utf8mb4')

try:
    with conn.cursor() as cursor:
        sql = 'update book_best set book_title = %s, book_author = %s, book_url =%s where term= %s and kind =%s and book_rank= %s '

        for i in range(len(book_title)):
            cursor.execute(sql, ( book_title[i], book_author[i], go[i],'month', 'it', i+1))
    conn.commit()


finally:
    conn.close()

print(book_title)
print(book_author)
print(go)


