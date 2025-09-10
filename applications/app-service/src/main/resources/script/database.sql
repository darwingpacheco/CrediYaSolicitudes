
CREATE TABLE IF NOT EXISTS estados (
                         id_estado BIGSERIAL PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         descripcion VARCHAR(255)
);

INSERT INTO estados (id_estado, nombre, descripcion)
VALUES
    (1, 'PENDIENTE', 'Pendiente de revisión'),
    (2, 'APROBADO', 'El préstamo fue aprobado'),
    (3, 'RECHAZADO', 'El préstamo fue rechazado')
ON CONFLICT (id_estado) DO NOTHING;

CREATE TABLE IF NOT EXISTS tipo_prestamo (
                               id_tipo_prestamo BIGSERIAL PRIMARY KEY,
                               nombre VARCHAR(100) NOT NULL,
                               monto_minimo NUMERIC(15,2) NOT NULL,
                               monto_maximo NUMERIC(15,2) NOT NULL,
                               tasa_interes NUMERIC(5,2) NOT NULL,
                               validacion_automatica BOOLEAN DEFAULT FALSE
);

INSERT INTO tipo_prestamo (
    id_tipo_prestamo,
    nombre,
    monto_minimo,
    monto_maximo,
    tasa_interes,
    validacion_automatica
) VALUES
      (1, 'Préstamo Personal', 1000000, 20000000, 0.18, TRUE),
      (2, 'Préstamo Vehicular', 5000000, 80000000, 0.15, TRUE),
      (3, 'Préstamo Hipotecario', 20000000, 500000000, 0.12, FALSE),
      (4, 'Préstamo de Libre Inversión', 2000000, 30000000, 0.20, TRUE)
ON CONFLICT (id_tipo_prestamo) DO NOTHING;

CREATE TABLE IF NOT EXISTS solicitud (
                           id_solicitud BIGSERIAL PRIMARY KEY,
                           documento_identidad VARCHAR(255) NOT NULL,
                           monto NUMERIC(15,2) NOT NULL,
                           plazo INT NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           id_estado INT NOT NULL,
                           id_tipo_prestamo INT NOT NULL,
                           CONSTRAINT fk_estado FOREIGN KEY (id_estado) REFERENCES estados(id_estado),
                           CONSTRAINT fk_tipo_prestamo FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo(id_tipo_prestamo)
);
