-- +goose Up
SELECT 'up SQL query';
DROP VIEW IF EXISTS user_view;
CREATE OR REPLACE VIEW user_view AS
SELECT Usrn.*, Usra.name, Usra.attrs, RolesOfUser.roles
FROM user_name Usrn
         LEFT JOIN LATERAL
    (SELECT u1.user_name,
            coalesce(array_agg(Role.role) FILTER ( WHERE Role.role IS NOT NULL ), '{}')::text[] AS roles
     FROM user_name u1
              LEFT JOIN user_has_roles uhr
                        ON u1.user_name = uhr.user_name
              LEFT JOIN role Role
                        ON uhr.role = Role.role
     GROUP BY u1.user_name)
        RolesOfUser ON Usrn.user_name = RolesOfUser.user_name
    LEFT JOIN user_attrs Usra ON Usrn.user_name = Usra.user_name;

-- +goose Down
SELECT 'down SQL query';
DROP VIEW IF EXISTS user_view;