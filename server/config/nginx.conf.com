limit_req_zone $binary_remote_addr zone=placra:10m rate=5r/s;

server {
    listen   80;
    server_name     acra.armorblade.com;
    gzip on;
    limit_req zone=placra burst=10;

    access_log  /var/log/nginx/acra.access.log;
    error_log   /var/log/nginx/acra.error.log;

    location /incupdate {
        proxy_pass      http://127.0.0.1:19000/incupdate;
        proxy_set_header  X-Real-IP  $remote_addr;
    }

    location / {
        proxy_pass http://127.0.0.1:5984/;
        proxy_set_header  X-Real-IP  $remote_addr;
    }


}