-- Usuario admin inicial (login admin, senha admin)
insert into usuarios (login, senha, perfil, ativo)
select 'admin', '$2a$10$AYILLp7uvXaZP2xKzPlyz.SLXA7o43sx06Q9i4LVGlZUDBzy9Gh2K', 'ADMIN', 1
from dual
where not exists (select 1 from usuarios where login = 'admin');
