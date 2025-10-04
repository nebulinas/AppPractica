package com.example.apppractica;

public class ComputadoraHistorial {

        private long id;
        private String tipo;
        private String nombreEquipo;
        private String numeroActivo;
        private String agencia;
        private String encargado;
        private String ip;
        private String ram;
        private String antivirus;
        private String windows;
        private String fechaCambio;
        private String fechaRegistro;
        private String marca;
        private String modelo;
        private String serie;
        private String estado;
        private String fechaAdquisicion;


        // Constructor
        public ComputadoraHistorial(long id, String nombreEquipo, String numeroActivo, String agencia, String encargado, String ip, String ram, String antivirus, String windows, String fechaCambio) {
            this.id = id;
            this.tipo = "COMPUTADORA";
            this.nombreEquipo = nombreEquipo;
            this.numeroActivo = numeroActivo;
            this.agencia = agencia;
            this.encargado = encargado;
            this.ip = ip;
            this.ram = ram;
            this.antivirus = antivirus;
            this.windows = windows;
            this.fechaCambio = fechaCambio;
        }

        public ComputadoraHistorial(long id, String activo, String tipoUtilitario, String marca,
                                      String modelo, String serie, String agencia, String encargado, String estado,
                                     String fechaRegistro, String fechaAdquisicion) {
            this.id = id;
            this.tipo = tipoUtilitario;
            this.nombreEquipo = tipoUtilitario;
            this.numeroActivo = activo;
            this.agencia = agencia;
            this.encargado = encargado;
            this.modelo= modelo;
            this.marca = marca;
            this.serie= serie;
            this.estado = estado;
            this.fechaRegistro = fechaRegistro;
            this.fechaAdquisicion = fechaAdquisicion;
            this.fechaCambio = fechaRegistro;
        }

        public String getTipo() {
            return tipo;
        }






        // Getters
        public long getId() { return id; }
        public String getNombreEquipo() { return nombreEquipo; }
        public String getNumeroActivo() { return numeroActivo; }
        public String getAgencia() { return agencia; }
        public String getEncargado() { return encargado; }
        public String getIp() { return ip; }
        public String getRam() { return ram; }
        public String getAntivirus() { return antivirus; }
        public String getWindows() { return windows; }
        public String getFechaCambio() { return fechaCambio; }

        public String getModelo() { return modelo; }
        public String getMarca() { return marca; }
        public String getSerie() { return serie; }
        public String getEstado() { return estado; }
        public String getFechaRegistro() { return fechaRegistro; }
        public String getFechaAdquisicion() { return fechaAdquisicion; }




}