USE tpi_prog2;

-- 1. INSERCIÓN DE PACIENTES (10 REGISTROS)
INSERT INTO paciente (nombre, apellido, dni, fecha_nacimiento)
VALUES 
('Carlos', 'Perez', '30123456', '1985-05-15'),
('Rodrigo', 'Mendez', '31987654', '1986-11-20'),
('Ana', 'Gomez', '32543210', '1990-03-25'),
('Martin', 'Lopez', '33876543', '1982-12-10'),
('Sofia', 'Ruiz', '34112233', '1995-07-01'),
('Javier', 'Diaz', '35776655', '1975-01-20'),
('Lucia', 'Torres', '36334455', '2001-09-08'),
('Diego', 'Castro', '37009988', '1988-04-12'),
('Elena', 'Vega', '38555444', '1999-11-30'),
('Hugo', 'Flores', '39666777', '1970-06-18');


-- 2. INSERCIÓN DE HISTORIAS CLÍNICAS (10 REGISTROS CON OBSERVACIONES)
INSERT INTO historia_clinica (nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id)
VALUES
('HC-0001', 'A+', 'Alergia al polen.', 'Loratadina 10mg.', 'Seguimiento anual con especialista.', 1), 
('HC-0002', 'O-', 'Cirugía de apéndice en 2010.', 'Ninguna.', 'Sin seguimiento pendiente; revisión general en 6 meses.', 2),
('HC-0003', 'B+', 'Asma infantil.', 'Salbutamol.', 'Control pulmonar cada 6 meses.', 3),
('HC-0004', 'AB-', 'Fractura de tibia (2018).', 'Ninguna.', 'Rehabilitación física finalizada. Alta médica.', 4),
('HC-0005', 'O+', 'Alergia a la penicilina.', 'Ninguna.', 'Anotar la alerta de alergia en ficha frontal del sistema.', 5),
('HC-0006', 'A-', 'Hipertensión leve.', 'Enalapril 5mg.', 'Monitoreo de presión arterial diario.', 6),
('HC-0007', 'B-', 'Vacunación completa.', 'Ninguna.', 'Paciente pediátrico, estado general estable.', 7),
('HC-0008', 'AB+', 'Amigdalectomía.', 'Ninguna.', 'Se recomienda dieta blanda por 48 horas post-consulta.', 8),
('HC-0009', 'A+', 'Intolerancia a la lactosa.', 'Suplementos Vit.', 'Revisar niveles de vitamina D en el próximo control.', 9),
('HC-0010', 'O-', 'Apendicitis, obesidad grado 1.', 'Metformina.', 'Derivación a nutricionista pendiente. Cita para la próxima semana.', 10);