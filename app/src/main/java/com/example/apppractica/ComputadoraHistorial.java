package com.example.apppractica;

public class ComputadoraHistorial {

        private long id;
        private String nombreEquipo;
        private String numeroActivo;
        private String agencia;
        private String encargado;
        private String ip;
        private String ram;
        private String antivirus;
        private String windows;
        private String fechaCambio;

        // Constructor
        public ComputadoraHistorial(long id, String nombreEquipo, String numeroActivo, String agencia, String encargado, String ip, String ram, String antivirus, String windows, String fechaCambio) {
            this.id = id;
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
}
