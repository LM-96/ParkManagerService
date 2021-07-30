var status_indoor = document.getElementById("status_indoor");

const weight_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/weightsensor/'
);

weight_socket.onmessage = function (e) {
    console.log(e)
    const data = JSON.parse(e);
    console.log(data)
    status_indoor.innerHTML = data;

}
