var mysql = require('mysql');

var pool;

experts.connect = function(){
  pool = mysql.createPool({
    connectionLimit: 100,
    host    :'localhost'.
    user    : 'root'
    password:'bestfood'
    database:'pandafurniture'
  });
}

experts.get = function(){
  return pool;
}
