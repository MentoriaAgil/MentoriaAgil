# Estágio de Build
FROM node:20-alpine AS build
WORKDIR /app

# Copia os arquivos de dependência primeiro para otimizar o cache das camadas
COPY package*.json ./
RUN npm ci

# Copia TODOS os arquivos do projeto (essencial para incluir .postcssrc.json e tailwind.config.js)
# Isso permite que o Tailwind v4 processe os estilos corretamente durante o build do Angular
COPY . .

# Executa o build de produção
RUN npm run build -- --configuration=production

# Estágio de Execução (Servidor Nginx)
FROM nginx:alpine

# Configuração de segurança: cria um grupo e usuário sem privilégios de root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia os artefatos gerados pelo Angular para o diretório padrão do Nginx
# Nota: O caminho '/app/dist/mentoria-agil/browser' baseia-se no seu angular.json
COPY --from=build /app/dist/mentoria-agil/browser /usr/share/nginx/html

# Copia o arquivo de configuração personalizada do Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Ajusta as permissões de pastas necessárias para que o Nginx funcione com um usuário não-root
RUN chown -R appuser:appgroup /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx /tmp && \
    mkdir -p /var/cache/nginx/client_temp && \
    chown -R appuser:appgroup /var/cache/nginx

# Define o usuário que executará o processo
USER appuser

# Expõe a porta 8081 conforme definido no seu arquivo nginx.conf
EXPOSE 8081

# Inicia o Nginx apontando o PID e os logs para locais onde o usuário tem permissão de escrita
CMD ["nginx", "-g", "daemon off; pid /tmp/nginx.pid; error_log /dev/stdout info;", "-c", "/etc/nginx/nginx.conf"]
