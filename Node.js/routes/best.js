var express = require('express');
var formidable = require('formidable');
var db = require('../db')
var router = express.Router();

//book/list 도서리스트 보여주기
router.get('/list', function (req, res, next) {
    var term = req.query.term;
    var kind = req.query.kind;


    var sql =
        "select * from book_best " +
        "where term = ? and kind = ?"
        "order by book_rank " ;
    
    console.log("sql : " + sql);
    console.log("term, kind : " + term, kind);

    var params = [term, kind];
    
    db.get().query(sql, params, function (err, rows) {
        if (err) {
            console.log("err"+err);
            return res.sendStatus(400);
        };

        console.log("rows : " + JSON.stringify(rows));
        res.status(200).json(rows);
    });
});


module.exports = router;