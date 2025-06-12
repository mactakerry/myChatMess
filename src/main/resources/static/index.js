const message = 'dawdw'
const recipient = 'Ариет';

sendMessage(message, recipient);

function sendMessage(message, recipient) {
    fetch('http://localhost:8080/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ text: message, sender: 'User', recipient: recipient})
    })
        .then(res => res.text())
        .then(data => {
            console.log(data);
        })
        .catch(err => console.log(err));
}