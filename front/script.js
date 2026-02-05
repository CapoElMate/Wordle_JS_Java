let filaActual = 0;
let letraActual = 0;
const intentosMaximos = 5;
const letrasPorPalabra = 5;



let palabraSecreta = ""; //luego va a ser una consulta al servidor

let celdas_estado = []; // array que guarda el estado de cada letra del intento actual

// Lógica para inicializar el juego
function inicializarJuego() {
      
    //crear la interfaz del juego
    crearInterfazJuego();
    asignarPalabraSecreta();
    asignarEventos();
}

function crearInterfazJuego() {
    const cuerpo = document.body;
    

    const filas = document.createElement('div');
    filas.className = 'cuadricula-5x5';
    for (let j = 0; j < intentosMaximos; j++) 
        {
        const fila = document.createElement('div');
        fila.className = 'fila';

        for (let i = 0; i < letrasPorPalabra; i++) 
            {
            const celda = document.createElement('div');
            celda.className = 'celda';
            celda.id = `fila${j}-celda${i}`;
            
            fila.appendChild(celda);
        }
        filas.appendChild(fila);
    }
    
    cuerpo.appendChild(filas);

}

function asignarEventos(){
    document.addEventListener('keydown', manejarTecla);

    //otros eventos aqui
}

function manejarTecla(evento) {
    const tecla = evento.key.toUpperCase();
    // console.log(`Tecla presionada: ${tecla}`);
    if (tecla === 'ENTER') {
        enviarPalabra();
    } else if (tecla === 'BACKSPACE') {
        borrarLetra();
    } else if (/^[A-Z]$/.test(tecla)) {
        agregarLetra(tecla);
    }
}

function agregarLetra(letra) {
    const fila = document.querySelectorAll('.fila')[filaActual];
    const celdas = fila.querySelectorAll('.celda');
    for (let i = 0; i < celdas.length; i++) {
        if (celdas[i].textContent === '') {
            celdas[i].textContent = letra;
            break;
        }
    }
}

function borrarLetra() {
    const fila = document.querySelectorAll('.fila')[filaActual];
    const celdas = fila.querySelectorAll('.celda'); 
    for (let i = celdas.length - 1; i >= 0; i--) {
        if (celdas[i].textContent !== '') {
            celdas[i].textContent = '';
            break;
        }
    }
}

async function enviarPalabra() {  
    const fila = document.querySelectorAll('.fila')[filaActual];
    const celdas = fila.querySelectorAll('.celda');
    let palabraIntento = '';

    for (let i = 0; i < celdas.length; i++) {
        palabraIntento += celdas[i].textContent;
    }

    if (palabraIntento.length < letrasPorPalabra) {
        alert(`La palabra debe tener ${letrasPorPalabra} letras.`);
        return;
    }
    celdas_estado = [];

    const esValida = await esPalabraValida(palabraIntento);
    if (esValida === false) {
        alert('La palabra no es válida.');
        return;
    }
    celdas_estado = await evaluarPalabra(palabraIntento);
    actualizarInterfaz(celdas, celdas_estado);
    
    if (celdas_estado.every(estado => estado === 'ok')) {
        alert('¡Felicidades! Has adivinado la palabra.');
    }

    filaActual++;
    if (filaActual >= 5) {
        alert('Juego terminado. No adivinaste la palabra.');
        document.removeEventListener('keydown', manejarTecla);
    }
}


function actualizarInterfaz(celdas, celdas_estado) {
    for (let i = 0; i < celdas.length; i++) {
        if (celdas_estado[i] === 'ok') {
            celdas[i].className = 'celda_ok';
        } else if (celdas_estado[i] === 'casi') {
            celdas[i].className = 'celda_casi';
        } else {
            celdas[i].className = 'celda_mal';
        }
    }
}

async function evaluarPalabra(palabraIntento) {
    // Evalua cada letra del intento , devuelve un array con el estado de cada letra
    // 'ok' , 'casi', 'mal'
    // hago la verificiacion en el servidor : 

    //Get al servidor con la palabra intento http://localhost:8080/api/guess?guess=palabraIntento
    // el servidor devuelve un array con el estado de cada letra

    const request = new Request(`http://localhost:8080/api/guess?guess=${encodeURIComponent(palabraIntento)}`);

    try {
        const response = await fetch(request);
        const data = await response.json();

        if (data === "Palabra no valida") {
            alert('La palabra no es válida.');
            return [];
        }

        return data.result.map(element => element + "");
    } catch (error) {
        alert('La palabra no es válida.');
        return [];
    }
}
// for (let i = 0; i < 5; i++) {
//         if (palabraIntento[i] === palabraSecreta[i]) {
//             celdas_estado.push('ok');
//         } else if (palabraSecreta.includes(palabraIntento[i])) {
//             celdas_estado.push('casi');
//         } else {
//             celdas_estado.push('mal');
//         }
//     }



async function esPalabraValida(palabra) {
    // aca tengo q implementar la lógica para verificar si la palabra es válida.
    // Por simplicidad, asumimos que cualquier palabra de 5 letras es válida.
    // luego tendria que hacer que la verificacion se haga en el servidor
    const request = new Request(`http://localhost:8080/api/esPalabraValida?palabra=${encodeURIComponent(palabra)}`);

    
    try {
        const response = await fetch(request);
        const data = await response.json();
        
        if (data === true) {
            return true;
        }
        return false;

    } catch (error) {
        console.error('Error al verificar la palabra:', error);
        alert('Error!', error);
        return false;
    }
}

function asignarPalabraSecreta() {
    palabraSecreta = "PERRO";

    listaPalabras = ["GATOS", "CASAS", "ARBOL", "LIBRO", "PLANO", "SILLA", "MESA", "LAPIZ", "CIELO"];

    const indiceRandom = Math.floor(Math.random() * listaPalabras.length);
    palabraSecreta = listaPalabras[indiceRandom];

    console.log(`Palabra secreta asignada: ${palabraSecreta}`);

}

document.addEventListener('DOMContentLoaded', inicializarJuego);