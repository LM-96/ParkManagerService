
const carparking_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/carparking/'
);


carparking_socket.onmessage = function (e) {
    console.log(e.data)
    const json_data = JSON.parse(e.data)
    console.log(json_data)
}   


function colorParkingSlot(slots){

}
