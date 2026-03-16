FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY angular.json .
COPY tsconfig*.json .
COPY src ./src
RUN npm run build --configuration=production

FROM nginx:alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/dist/mentoria-agil/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

RUN chown -R appuser:appgroup /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx /tmp
RUN mkdir -p /var/cache/nginx/client_temp && chown -R appuser:appgroup /var/cache/nginx

USER appuser
EXPOSE 8081

CMD ["nginx", "-g", "daemon off; pid /tmp/nginx.pid; error_log /dev/stdout info;", "-c", "/etc/nginx/nginx.conf"]
