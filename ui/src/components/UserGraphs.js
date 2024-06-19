import React, { useState, useContext, useEffect } from 'react';
import { Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { DeleteOutlined } from '@ant-design/icons';
import { UserContext } from './UserContex';
import './UserGraphs.css'
import PropTypes from 'prop-types';



function UserGraphs(props) {
    const [isOpen, setIsOpen] = useState(false);
    const { userID } = useContext(UserContext);
    const [userGraphs, setUserGraphs] = useState(null);


    const getAllUserGraphs = () => {
      setIsOpen(true); 
      props.setIsUserGraphMode(true);
      fetch('http://localhost:8080/get_all_graphs?user_id=' + userID, {
          method: 'GET',
          headers: {
              'Content-Type': 'application/json',
              'Connection': 'keep-alive'
          }
        })
        .then(response => response.json())
        .then(graphs => setUserGraphs(graphs))
        .catch(error => console.log(error));
    }


    const getGraphById = (graphId) => {
        fetch('http://localhost:8080/get_graph?graph_id=' + graphId, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Connection': 'keep-alive'
            }
          })
          .then(response => response.json())
          .then(graph => displayGraph(graph['graph']))
          .catch(error => console.log(error));
    }


    const deleteGraphById = (graphId) => {
        fetch('http://localhost:8080/delete_graph?graph_id=' + graphId, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Connection': 'keep-alive'
            },
            body: {}
          })
          .catch(error => console.log(error));
          delete userGraphs[graphId];
    }


    const displayGraph = (graph) => {
        var vertices = graph['vertices'];
        var edges = graph['edges'];

        const radius = 300;
        const centerX = 800;
        const centerY = 400;
        const angle = Math.PI * 2 / vertices.length;
        for (var i = 0; i < vertices.length; ++i){
            vertices[i].x = centerX + radius * Math.cos(angle * i);
            vertices[i].y = centerY + radius * Math.sin(angle * i);
            vertices[i].data = vertices[i].index.toString();
        }
        for (var j = 0; j < edges.length; ++j){
            edges[j] = {source: edges[j].source.index,
                        target: edges[j].target.index,
                        id: j + 1,
                        weight: edges[j].weight,
                        data: edges[j].data
                        }
        }

        props.addVertex(vertices);
        props.addEdge(edges);
        props.updateVertexCount(vertices.length + 1);
        props.setEdgeCounter(edges.length + 1);
        props.setIsDirected(graph['info']['isDirected']);
        props.setIsWeighted(graph['info']['isWeighted']);
        props.setHasSelfLoops(graph['info']['hasSelfLoops'] | 0);
        props.sethasMultipleEdges(graph['info']['hasMultipleEdges'] | 0);
        props.setGraphName(graph['info']['graphName']);

        clear();
        setIsOpen(false);
    }


    useEffect (() => {
        if (!isOpen){
            props.setIsUserGraphMode(false);
        }
    })


    const clear = () => {
        const canvas = document.getElementById('canvas');
        const ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }


    return (
      <div>
         <Button className='button' onClick={getAllUserGraphs} type='text' block> Graphs </Button>
         {
          isOpen &&   <div className='UserGraphs'>
                          <div className='UserGraphs-body'>
                            <div style={{float: 'right'}}>
                            <Button onClick={() => setIsOpen(false)} icon={<CloseOutlined/>}></Button>
                            </div>
                            <div style={{marginTop: '40px'}}>
                                {userGraphs ? Object.entries(userGraphs).map(([id, value]) => {
                                    return( id !== 'ids' ?
                                        <div style={{paddingTop: '1rem'}}>
                                            <Button style={{paddingLeft: '130px', paddingRight: '130px'}} onClick={() => getGraphById(id)}>{value}</Button>
                                            <Button style={{float: 'right'}} onClick={() => deleteGraphById(id)} icon={<DeleteOutlined/>}></Button>
                                        </div> : null)
                                }) : null}
                            </div>
                          </div>
                      </div>
          }
      </div>
    )
}

export default UserGraphs;

UserGraphs.propTypes = {
    addVertex: PropTypes.func,
    addEdge: PropTypes.func,
    updateVertexCount: PropTypes.func,
    setEdgeCounter: PropTypes.func,
    setIsDirected: PropTypes.func,
    setIsWeighted: PropTypes.func,
    setHasSelfLoops: PropTypes.func,
    sethasMultipleEdges: PropTypes.func,
    setIsUserGraphMode: PropTypes.func,
    setGraphName: PropTypes.func
}