var path = require('path');

var exp = module.exports;

exp.appName = 'incupdate';

exp.logger = {
    "appenders": [
        {
            "type": "console",
            "layout": {
                "type": "colored"
            }
        },
        {
            "type": "file",
            "filename": path.join(__dirname, "/logs", exports.appName + ".log"),
            "pattern": "connector",
            "maxLogSize": 1048576,
            "layout": {
                "type": "basic"
            },
            "backups": 5,
            "category": 'incupdate'
        }
    ],

    "levels": {
        "incupdate": "DEBUG"
    },

    "replaceConsole": true,

    "lineDebug": true

}

exp.incupdateDataDir = path.join(__dirname, '../../client/incupdatedata')
exp.diffDir = 'incupdate';
exp.updateFileName = 'update.zip';
exp.updateLimitFileName = 'updatelimit.json';
exp.notSupportedUpdateFile = path.join(__dirname, 'not-supported-update.zip');
