//Write The organization employs several operators. The operator can serve only one client, the rest must wait in line. The client can hang up and call back again after a while.
// The operator can serve several clients in a row, but only one at a time. The operator can not serve more than 3 clients in a row.
// The operator can not serve more than 5 clients in a day.
// The operator can not serve more than 3 clients in a row.

package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)	

type Operator struct {
	id int
	clients int
}

type Client struct {
	id int
}

func (o *Operator) Serve(c *Client) {
	fmt.Printf("Operator %d is serving client %d\n", o.id, c.id)
	o.clients++
}

func (o *Operator) Finish(c *Client) {
	fmt.Printf("Operator %d finished serving client %d\n", o.id, c.id)
	o.clients--
}

func (o *Operator) IsAvailable() bool {
	return o.clients < 3
}

func (o *Operator) IsOverworked() bool {
	return o.clients >= 3
}

func (o *Operator) IsOverworkedForTheDay() bool {
	return o.clients >= 5
}

func (c *Client) Call() {
	fmt.Printf("Client %d is calling\n", c.id)
}

func (c *Client) HangUp() {
	fmt.Printf("Client %d is hanging up\n", c.id)
}

func main() {
	var wg sync.WaitGroup
	wg.Add(1)
	operators := make([]*Operator, 3)
	for i := 0; i < 3; i++ {
		operators[i] = &Operator{id: i}
	}
	clients := make([]*Client, 10)
	for i := 0; i < 10; i++ {
		clients[i] = &Client{id: i}
	}
	go func() {
		defer wg.Done()
		for _, c := range clients {
			c.Call()
			for _, o := range operators {
				if o.IsAvailable() {
					o.Serve(c)
					time.Sleep(time.Duration(rand.Intn(5)) * time.Second)
					o.Finish(c)
					break
				}
			}
			c.HangUp()
		}
	}()
	wg.Wait()
}
