var status_timer_dtfree = document.getElementById("status_dtfreetimer");

const timerdtfree_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/timerdtfree/'
);

timerdtfree_socket.onmessage = function (e) {
    console.log(e)

    const json_data = JSON.parse(e.data);
    console.log(json_data)

    status_timer_dtfree.innerHTML = json_data.data
}

