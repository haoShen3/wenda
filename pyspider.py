from pyspider.libs.base_handler import *
import pymysql
import random


class Handler(BaseHandler):
    crawl_config = {
        "headers": {
            "User-Agent": "GoogleBot",
            "Host": "www.zhihu.com",
        }
    }

    # 建立数据库连接
    def __init__(self):
        self.db = pymysql.connect('localhost', 'root', 'mysqljianghao0801', 'wenda')

    # 添加问题
    def add_question(self, title, content, comment_count):
        try:
            cursor = self.db.cursor()
            sql = 'insert into question(title, content, user_id, created_date, comment_count) values ("%s", "%s", %d, now(), %d)' % (
            title, content, random.randint(1, 30), comment_count)
            cursor.execute(sql)
            qid = cursor.lastrowid
            self.db.commit()
            return qid
        except Exception as e:
            print(e)
            self.db.rollback()
        return 0

    # 添加评论
    def add_comment(self, qid, content):
        try:
            cursor = self.db.cursor()
            sql = 'insert into comment(content, entity_type, entity_id, user_id, created_date, status) values ("%s", 1, %d, %d, now(), %d)' % (
            content, qid, random.randint(1, 30), 0)
            cursor.execute(sql)
            self.db.commit()
        except Exception as e:
            print(e)
            self.db.rollback()
        return 0

    # 初始 url 路口
    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('https://www.zhihu.com/topic/19591985/top-answers', callback=self.index_page, validate_cert=False)

    # 爬取页面中的 url
    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):
        for each in response.doc('h2.ContentItem-title>div>a[target=_blank]').items():
            self.crawl(each.attr.href, callback=self.detail_page, validate_cert=False)

    # 爬取页面中的各种细节
    @config(priority=2)
    def detail_page(self, response):
        decription = response.doc("div.QuestionRichText.QuestionRichText--collapsed span.RichText.ztext").text()
        title = response.doc('h1.QuestionHeader-title').text()
        comment = response.doc('span.RichText.ztext.CopyrightRichText-richText').text()
        decription = decription.replace('"', '\\"')
        qid = self.add_question(title, decription, 1)
        comment = comment.replace('"', '\\"')[:45]
        self.add_comment(qid, comment)
        return {
            "url": response.url,
            "title": title,
            "html": decription,
            "comment": comment,
        }