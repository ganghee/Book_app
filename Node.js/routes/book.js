var express = require('express');
var formidable = require('formidable');
var db = require('../db')
var router = express.Router();

var LOADING_SIZE = 20;
//book/info 도서 정보 입력하기
router.post('/info/insert', function (req, res, next) {
    if (!req.body.member_seq) {
        return res.sendStatus(400);
    }

    var member_seq = req.body.member_seq;
    var name = req.body.name;
    var description = req.body.description;
    var publisher = req.body.publisher;


    var sql_insert =
        "insert into book_info (member_seq, name, description, publisher) " +
        "values(?, ?, ?, ?); ";

    console.log(sql_insert);

    var params = [member_seq, name, description,publisher];
    console.log(params);
    db.get().query(sql_insert, params, function (err, result) {
        if (err) console.log(err);
        else{console.log("rows 값 " + JSON.stringify(result));
        res.status(200).send('' + result.insertId);
            }
    });
});

//book/info/update 도서 정보 수정하기
router.post('/info/update', function (req, res, next) {
    if (!req.body.member_seq) {
        return res.sendStatus(400);
    }

    var seq = req.body.seq;
    var name = req.body.name;
    var description = req.body.description;
    var publisher = req.body.publisher;


    var sql_update =
        "update book_info set name = ?, description = ?, publisher = ? "+
        "where seq = ?"

    console.log(sql_update);

    var params = [name, description, publisher, seq];

    db.get().query(sql_update, params, function (err, result) {
        console.log("result 값 " + JSON.stringify(result));
        console.log(result.insertId);
        res.status(200).send('' + result.insertId);
    });
});

//book/info/image 도서 이미지 설정하기
router.post('/info/image', function (req, res) {
    var form = new formidable.IncomingForm();

    form.on('fileBegin', function (name, file) {
        file.path = './public/img/' + file.name;
    });

    form.parse(req, function (err, fields, files) {
        var sql_update = "update book_info set book_info_filename = ? where seq = ?;";

        db.get().query(sql_update, [files.file.name,fields.info_seq], function (err, rows) {
            res.sendStatus(200);
        });
    });
});

//book/info/:seq 도서 상세정보 보여주기
router.get('/info/:seq', function (req, res, next) {
    var seq = req.params.seq;
    var member_seq = req.query.member_seq;

    var sql =
        "select a.*, " +
        "  '0' as favorite, " +
        "  if( exists(select * from book_keep where member_seq = ? and a.seq = info_seq), 'true', 'false') as is_keep " +
        
        "from book_info as a " +
        "where seq = ? ; ";
    console.log("sql : " + sql);

    db.get().query(sql, [member_seq, seq], function (err, rows) {
        if (err) return res.sendStatus(400);

        console.log("rows : " + JSON.stringify(rows));
        res.json(rows[0]);
    });
});

//book/list 도서리스트 보여주기
router.get('/list', function (req, res, next) {
    var member_seq = req.query.member_seq;
    var order_type = req.query.order_type;
    var current_page = req.query.current_page || 0;

    if (!member_seq) {
        return res.sendStatus(400);
    }

    var order_add = '';

    if (order_type) {
        order_add = order_type + ' desc, reg_date desc';
    } else {
        order_add = 'reg_date desc';
    }

    var start_page = current_page * LOADING_SIZE;


    var sql =
        "select *, if( exists(select * from book_keep "+
        "where member_seq = ? and info_seq = book_info.seq), 'true', 'false') as is_keep "+
        "from book_info " +
        "order by  " + order_add + " " +
        "limit ? , ? ; ";
    console.log("sql : " + sql);
    console.log("order_add : " + order_add);

    var params = [member_seq,start_page, LOADING_SIZE];
    
    db.get().query(sql, params, function (err, rows) {
        if (err) {
            console.log("err"+err);
            return res.sendStatus(400);
        };

        console.log("rows : " + JSON.stringify(rows));
        res.status(200).json(rows);
    });
});

//book/list/:info_seq 즐겨찾기 도서정보 삭제하기
//삭제는 Path로 파라미터를 전달한다.
router.delete('/list/:member_seq/:seq', function(req, res, next) {
    var member_seq = req.params.member_seq;
    var seq = req.params.seq;
    
    console.log(member_seq);
    console.log(seq);

    if (!member_seq || !seq) {
        console.log("ERR"+err);
        return res.sendStatus(400);
        
    }

    var sql_delete = "delete from book_info where member_seq = ? and seq = ? ";

    db.get().query(sql_delete, [member_seq, seq], function (err, rows) {
            if (err) {
                console.log(err);
                return res.sendStatus(400);
            }
            res.sendStatus(200);
    });
});


module.exports = router;
