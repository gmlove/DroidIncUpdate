var exp = module.exports;
var Promise = require('promise');
var fs = require('fs');

var fs_exists = function(file, cb) {
    fs.exists(file, function(exists){
        cb(null, exists);
    });
}

exp.fs_exists = Promise.denodeify(fs_exists);
