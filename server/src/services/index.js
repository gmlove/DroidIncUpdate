var express = require('express');
var router = express.Router();
var logger = require('../logger').getLogger('incupdate', __filename);
var config = require('../config');
var crypto = require('crypto');
var path = require('path');
var fs = require('fs');
var zlib = require('zlib');
var mkdirp = require('mkdirp');
var Promise = require('promise');
var IncUpdateVersion = require('../models/IncUpdateVersion');
var promiseUtil = require('../utils/promiseUtil');

router.get('/update', function (req, res) {
    logger.info('update download: query=%j', req.query);
    var version = req.query.version;
    var pn = req.query.pn;
    if(!version || !pn) {
        logger.error('update download failed: version=%s, pn=', version, pn);
        return res.sendStatus(500);
    }
    var incUpdateVersions = [];
    var newestVersion = null;
    var appIncupdateDataDir = path.join(config.incupdateDataDir, pn);
    promiseUtil.fs_exists(appIncupdateDataDir).then(function(exists){
        if(!exists) {
            return Promise.resolve(false);
        } else {
            return Promise.denodeify(fs.readdir)(appIncupdateDataDir);
        }
    }).then(function (files) {
        if(!files) {
            return Promise.resolve(false);
        }
        files.forEach(function (dirname) {
            if(IncUpdateVersion.isIncUpdateVersion(dirname)){
                incUpdateVersions.push(new IncUpdateVersion(dirname, pn));
            }
        });
        incUpdateVersions.sort(function (v1, v2) {
            return v1.dirname > v2.dirname;
        });
        if(!incUpdateVersions.length) {
            return Promise.resolve(false);
        }

        function findUpdateVersion(incUpdateVersions, index) {
            logger.debug('findUpdateVersion: incUpdateVersions.length=%s, index=%s, pn=%s, ver=%s',
                incUpdateVersions.length, index, pn, version);
            if(index < 0) {
                return Promise.resolve(false);
            }
            return incUpdateVersions[index].isVersionSupported(req.query).then(function(supported){
                if(supported) {
                    return Promise.resolve(incUpdateVersions[index]);
                } else if(incUpdateVersions[index].version == version) {
                    return Promise.resolve(incUpdateVersions[index]);
                } else {
                    return findUpdateVersion(incUpdateVersions, index - 1);
                }
            })
        }

        return findUpdateVersion(incUpdateVersions, incUpdateVersions.length - 1);
    }).then(function (canUpdateNewestVersion){
        newestVersion = canUpdateNewestVersion;
        if(!newestVersion) {
            return Promise.resolve(false);
        }
        return newestVersion.getSupportedVersions();
    }).then(function (newestVersion) {
        if(!newestVersion) {
            return Promise.resolve(false);
        }
        var versions = newestVersion.supportedVersions;
        for (var i = 0; i < versions.length; i++) {
            if(version == versions[i].version) {
                return Promise.resolve(versions[i]);
            }
        }
        return Promise.resolve(false);
    }).done(function (targetVersion) {
        if(!targetVersion) {
            logger.info('update download[%s]: %s', version, config.notSupportedUpdateFile);
            return res.download(config.notSupportedUpdateFile);
        } else {
            var filePath = newestVersion.getUpdateFilePath(targetVersion.dirname);
            logger.info('update download[%s]: %s', version, filePath);
            return res.download(filePath);
        }
    }, function (err) {
        logger.error('update download failed: version=%s, err=', version, err);
        return res.sendStatus(500);
    });
});


module.exports = router;
