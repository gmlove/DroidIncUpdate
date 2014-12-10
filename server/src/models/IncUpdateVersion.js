var Promise = require('promise');
var fs = require('fs');
var path = require('path');
var config = require('../config');
var logger = require('../logger').getLogger('incupdate', __filename);
var promiseUtil = require('../utils/promiseUtil');


function IncUpdateVersion (dirname, pn) {
    this.dirname = dirname;
    this.pn = pn;
    this.achieveTime = null;
    this.version = null;
    this.supportedVersions = null;
    this.updateLimit = null;
    this._parseDirname(dirname);
}


var exp = module.exports = IncUpdateVersion;
var proto = exp.prototype;

var test_re = /^([0-9]{14})-([0-91-z]{32})$/;

IncUpdateVersion.isIncUpdateVersion = function (dirname) {
    return /^[0-9]{14}-[0-91-z]{32}$/.test(dirname)
}


proto._parseDirname = function (dirname) {
    var m = dirname.match(test_re);
    if(!m || !m[1] || !m[2]) {
        logger.error('_parseDirname failed: dirname=%s', dirname);
        throw new Error('_parseDirname failed.');
    }
    this.achieveTime = m[1];
    this.version = m[2];
}


proto.getSupportedVersions = function () {
    var self = this;
    this.supportedVersions = [];
    var diffDir = path.join(config.incupdateDataDir, this.pn, this.dirname, config.diffDir);
    return Promise.denodeify(fs.readdir)(diffDir).then(function (files) {
        files.forEach(function (dirname) {
            if(IncUpdateVersion.isIncUpdateVersion(dirname)) {
                self.supportedVersions.push(new IncUpdateVersion(dirname));
            }
        });
        return Promise.resolve(self);
    });
}

var copyQuery = function(query) {
    var ret = {};
    var keys = Object.keys(query);
    for (var i = 0; i < keys.length; i++) {
        ret[keys[i]] = query[keys[i]];
    }
}

var hashCode = function(str) {
    var hash = 0;
    if (str.length == 0) return hash;
    for (i = 0; i < str.length; i++) {
        char = str.charCodeAt(i);
        hash = ((hash<<5)-hash)+char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
}


proto.isVersionSupported = function (query) {
    var self = this;
    var updateLimitFile = path.join(config.incupdateDataDir, this.pn, this.dirname, config.updateLimitFileName);
    return promiseUtil.fs_exists(updateLimitFile).then(function(exists){
        if(exists) {
            return Promise.denodeify(fs.readFile)(updateLimitFile);
        } else {
            return Promise.resolve(false);
        }
    }).then(function(content){
        if(!content) {
            logger.debug('updateLimitFile: file=%s, content=%s', updateLimitFile, content);
            return Promise.resolve(true);
        }
        self.updateLimit = JSON.parse(content);
        logger.debug('updateLimitFile: file=%s, content=%j', updateLimitFile, self.updateLimit);
        if(self.updateLimit['prop_limit']) {
            var limitkeys = Object.keys(self.updateLimit['prop_limit']);
            for (var i = 0; i < limitkeys.length; i++) {
                if(!query[limitkeys[i]]) {
                    logger.debug('query has no key: ' + limitkeys[i]);
                    return Promise.resolve(false);
                } else {
                    var limit = self.updateLimit['prop_limit'][limitkeys[i]];
                    if(limit.indexOf(query[limitkeys[i]]) == -1) {
                        logger.debug('query limit not match: limit=%j, query[%s]=%s', limit, limitkeys[i], query[limitkeys[i]]);
                        return Promise.resolve(false);
                    }
                }
            }
        }
        var did = query['did'];
        if(self.updateLimit['percent_limit'] !== undefined) {
            if(!did) {
                return Promise.resolve(false);
            }
            var hash = Math.abs(hashCode(did) % 100);
            var percent = parseInt(self.updateLimit['percent_limit']);
            if(hash > percent) { // hash: 0-99
                return Promise.resolve(false);
            }
        }
        return Promise.resolve(true);
    });
}

proto.getUpdateFilePath = function (dirname) {
    return path.join(config.incupdateDataDir, this.pn, this.dirname, config.diffDir, dirname, config.updateFileName);
}

