<h1 align="center"> Apollo70 Filmes - API-User</h1>

![Badge](http://img.shields.io/static/v1?label=STATUS&message=DEVELOPMENT&color=yellow&style=for-the-badge)
![Badge](http://img.shields.io/static/v1?label=RELEASE%20DATE&message=SEPTEMBER%202022&color=yellow&style=for-the-badge)

<p align="center">O projeto Apollo70 Filmes consiste em uma aplicação de uma locadora de filmes. A aplicação é dividida em microserviços que detém as funcionalidades necessárias para o usuário ter acesso a um catálogo de filmes disponíveis para compra e alocação.<p>

## Fluxo de Funcionamento da Aplicação
Além do microserviço `User` há outros dois responsáveis pelo gerenciamento dos filmes e a gravação do histórico de compra e alocação, respectivamente:

 * [API-Movie-Manager](https://github.com/Team-Spring-Alpha/API-Movie-Manager)
 * [API-Allocation-History](https://github.com/Team-Spring-Alpha/API-Allocation-History.git)

O `Movie-Manager`tem conexão com a API externa [The Movie DB](https://www.themoviedb.org/documentation/api) que disponibiliza acesso a dados de filmes, atores e programas de TV. Já o `Allocation-History` basicamente pega os dados de compra/alocação de filmes e salva isso em um banco de dados Mongo. 

> Se você deseja saber mais sobre os microserviços Movie-Manager e Allocation-History acesse seus respectivos repositórios, aqui, nesse repositório iremos falar mais sobre o microserviço User que é a principal aplicação do projeto, sendo ela quem detém acesso aos outros microserviços e suas funcionalidades.

Além dos microserviços informados acima o `User` também detém acesso a um [Gateway de pagamento](https://github.com/rcdomingos/pb-process-payment) que é responável por simular a compra e/ou alocação dos filmes que o usuário deseja.

<img src="https://user-images.githubusercontent.com/97967193/189252252-6498579c-d4b0-4433-b9d3-5d0a130c452d.png" height="400" width="650" title="esquematizacao-fluxo">

## Funcionalidades

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | /api/user | Cadastra um novo usuário. |
| `GET` | /api/user | Busca todos os usuários cadastrados. |
| `GET` | /api/user/{id} | Busca o registro de um usuário. |
| `PATCH` | /api/user/{id} | Atualiza o registro de um usuário. |
| `PATCH` | /api/user/account{id} | Define o registro de um usuário como bloqueado. Usuários com registro bloqueado não podem se autenticar e consequentemente não podem alocar ou comprar filmes. |
| `GET` | /api/movie-manager/movie-search/?{filtros}| Busca por filmes com os filtros de nome, gênero, stream, data de lançamento e atores. |
| `GET` | /api/movie-manager/{id}/recommendations | Busca filmes recomendados conforme o id do filme passado como parâmetro. |
| `POST` | /api/auth/signin | Faz a autenticação do usuário e retorna um token de acesso. O usuário só poderá efetuar a alocação/compra de um filme se ele for autenticado. |
| `GET` | /api/auth/refresh/{email} | Faz o refresh do token de acesso. |
| `POST` | /api/movie-payment| Faz uma requisição de pagamento. |
| `PUT` | /api/allocation-history/{userId} | Busca o histórico de um determinado usuário. |

## Tecnologias utilizadas
- Spring Validation
- Spring Web
- Spring AMQP
- Spring OpenFeign
- Spring Security
- Spring Fox
- ModelMapper
- Lombok
- MongoDB
- Swagger
- JWT

> Para saber mais acesse a [Documentação](https://userapollo70.herokuapp.com/swagger-ui/index.html)
