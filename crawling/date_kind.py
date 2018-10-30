from selenium.webdriver.support.select import Select
from selenium import webdriver

path = 'C:/Users/01037/Documents/chromedriver_win32/chromedriver'
driver = webdriver.Chrome(path)
driver.get("http://digital.kyobobook.co.kr/digital/publicview/publicViewBest.ink?tabType=EBOOK&tabSrnb=12")
select = Select(driver.find_element_by_name("selectDateType"))


def month_book():
    select.select_by_visible_text('월간')

def week_book():
    select.select_by_visible_text('주간')

def total_book():
    select = Select(driver.find_element_by_name("largeCategory"))
    select.select_by_visible_text('분야 | 종합')

def novel_book():
    select = Select(driver.find_element_by_name("largeCategory"))
    select.select_by_visible_text('소설')

def self_book():
    select = Select(driver.find_element_by_name("largeCategory"))
    select.select_by_visible_text('자기계발')

def it_book():
    select = Select(driver.find_element_by_name("largeCategory"))
    select.select_by_visible_text('컴퓨터/인터넷')



