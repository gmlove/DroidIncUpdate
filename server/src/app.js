var express = require('express');
var config = require('./config');
var logger = require('./logger');
var path = require('path');

logger.configure(config.logger);
logger = logger.getLogger('incupdate', __filename);

var app = express();

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', '{views}');

app.use('/incupdate', require('./services'));

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    logger.error('req.path not found: ', req.path);
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500).send(JSON.stringify({
            message: err.message,
            error: err
        }));
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500).send(JSON.stringify({
        message: err.message,
        error: {}
    }));
});


app.listen(38000);

module.exports = app;


