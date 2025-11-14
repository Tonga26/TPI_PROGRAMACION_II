USE tpi_prog2;

INSERT INTO paciente (nombre, apellido, dni, fecha_nacimiento)
VALUES 
('Carlos', 'Perez', '30123456', '1985-05-15'),
('Rodrigo', 'Mendez', '31987654', '1986-11-20');

INSERT INTO historia_clinica (nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, paciente_id)
VALUES
('HC-0001', 'A+', 'Alergia al polen.', 'Loratadina 10mg.', 1), 
('HC-0002', 'O-', 'Cirugía de apéndice en 2010.', 'Ninguna.', 2);
