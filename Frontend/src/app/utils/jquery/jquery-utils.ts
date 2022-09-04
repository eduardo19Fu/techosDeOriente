declare const $: any;
// tslint:disable-next-line: no-string-literal
window['$'] = window['jQuery'] = $;

export class JqueryConfigs {
    constructor() { }

    // MÉTODO DE INICIALIZACIÓN DE DATATABLE ADMINLTE
    configDataTable(nombreTabla: string): void {
        $(() => {
            $(`#${nombreTabla}`).DataTable({
                destroy : true,
                responsive: false,
                lengthChange: true,
                autoWidth: false,
                buttons: ['excel'],
                language: {
                    lengthMenu : 'Mostrar _MENU_ registros',
                    paginate: {
                        first: 'Primero',
                        last: 'Último',
                        next: 'Siguiente',
                        previous: 'Anterior'
                    },
                    search: 'Búsqueda:',
                    info: 'Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros'
                }
            }).buttons().container().appendTo('#' + nombreTabla + '_wrapper .col-md-6:eq(0)');
        });
    }

    // MÉTODO DE INICIALIZACIÓN DE DATATABLE ADMILTE PARA MODAL PRODUCTOS
    configDataTableModal(nombreTabla: string): void {
        $(() => {
            $(`#${nombreTabla}`).DataTable({
                destroy : true,
                responsive: false,
                lengthChange: true,
                autoWidth: false,
                language: {
                    lengthMenu : 'Mostrar _MENU_ registros',
                    paginate: {
                        first: 'Primero',
                        last: 'Último',
                        next: 'Siguiente',
                        previous: 'Anterior'
                    },
                    search: 'Búsqueda:',
                    info: 'Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros'
                }
            }).buttons().container().appendTo('#' + nombreTabla + '_wrapper .col-md-6:eq(0)');
        });
    }

    // MÉTODO DE INICIALIZACION DE TOOLTIPS
    configToolTip(): void {
        $(() => {
            $('[data-toggle="tooltip"]').tooltip({
                trigger: 'hover'
            });

            $.widget.bridge('uibutton', $.ui.button);
        });
    }

    configSelect(): void {
        $(() => {
            // Initialize Select2 Elements
            $('.select2').select2();

            // Initialize Select2 Elements
            $('.select2bs4').select2({
                theme: 'bootstrap4'
            });
        });
    }

    hideModal(): void {
        $('#modal-movimiento').on('hidden.bs.modal', (e) => {

            $('#form-movimiento').find('input[type=text], input[type=number], select').val('');
        });
    }
}
