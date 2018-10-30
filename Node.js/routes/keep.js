var express = require('express');
var db = require('../db')
var router = express.Router();

//keep/list 즐겨찾기 리스트 보여주기
router.get('/list', function(req, res, next) {
  var member_seq = req.query.member_seq;
  
  console.log(member_seq);

  if (!member_seq) {
      return res.sendStatus(400);
  }

  var sql = 
    "select a.seq as keep_seq, a.member_seq as keep_member_seq, a.reg_date as keep_date, " + 
    "  b.*, " + 
    "  'true' as is_keep, " + 
    "  (select book_info_filename from book_info where seq = a.info_seq) as image_filename " + 
    "from book_keep as a left join book_info as b " + 
    " on (a.info_seq = b.seq) " + 
    "where a.member_seq = ? " + 
    "order by a.reg_date desc ";
  console.log("sql : " + sql);
    
  db.get().query(sql, [member_seq], function (err, rows) {
      if (err) return res.sendStatus(400);
      res.status(200).json(rows);
  }); 
});

//keep/:member_seq/:info_seq 즐겨찾기에 도서정보 추가하기
router.post('/:member_seq/:info_seq', function(req, res, next) {
    var member_seq = req.params.member_seq;
    var info_seq = req.params.info_seq;

    console.log(member_seq);
    console.log(info_seq);

    if (!member_seq || !info_seq) {
        return res.sendStatus(400);
    }

    var sql_select = "select count(*) as cnt from book_keep where member_seq = ? and info_seq = ?;";//즐겨찾기에 추가된 것이 있다면 오류
    var sql_insert = "insert into book_keep (member_seq, info_seq) values(?, ?);";
    var sql_update = "update book_info set keep_cnt = keep_cnt+1 where seq = ? ";
    
    db.get().query(sql_select, [member_seq, info_seq], function (err, rows) {
        if (rows[0].cnt > 0) {
            return res.sendStatus(400);
        }

        db.get().query(sql_insert, [member_seq, info_seq], function (err, rows) {
            db.get().query(sql_update, info_seq, function (err, rows) {
                if (err) return res.sendStatus(400);
                res.sendStatus(200);
            });
        });  
    });        
}); 

//keep/:member_seq/:info_seq 즐겨찾기 도서정보 삭제하기
router.delete('/:member_seq/:info_seq/', function(req, res, next) {
    var member_seq = req.params.member_seq;
    var info_seq = req.params.info_seq;
    
    console.log(member_seq);
    console.log(info_seq);

    if (!member_seq || !info_seq) {
        return res.sendStatus(400);
    }

    var sql_delete = "delete from book_keep where member_seq = ? and info_seq = ? ";
    var sql_update = "update book_info set keep_cnt = keep_cnt-1 where seq = ? ";
    db.get().query(sql_delete, [member_seq, info_seq], function (err, rows) {
        db.get().query(sql_update, info_seq, function (err, rows){
            if (err) return res.sendStatus(400);
            res.sendStatus(200);
        });
    });  
});

module.exports = router;