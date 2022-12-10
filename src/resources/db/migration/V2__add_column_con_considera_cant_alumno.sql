ALTER TABLE concepto ADD con_considera_cant_alumno INT(1) NOT NULL DEFAULT 0 
COMMENT 'Si considerará en el cálculo de pago la cantidad de alumnos del apoderado (ej: cuota de 15.000gs x 2 alumnos = 30.000 gs)';
