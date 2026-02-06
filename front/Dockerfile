from nginx:stable-alpine3.23-perl as nginx
WORKDIR /usr/share/nginx/html
COPY . .
RUN apk add --no-cache gettext
COPY env.template.js /usr/share/nginx/html/env.template.js
CMD ["/bin/sh","-c","envsubst < /usr/share/nginx/html/env.template.js > /usr/share/nginx/html/env.js && nginx -g 'daemon off;'"]