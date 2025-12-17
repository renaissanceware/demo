package com.example.demo.service;

import com.example.demo.entity.Channel;
import com.example.demo.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChannelService {
    
    @Autowired
    private ChannelRepository channelRepository;
    
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }
    
    public Optional<Channel> getChannelById(String id) {
        return channelRepository.findById(id);
    }
    
    public Channel saveChannel(@Valid Channel channel) {
        channel.setCreatedAt(LocalDateTime.now());
        return channelRepository.save(channel);
    }
    
    public Channel updateChannel(String id, @Valid Channel channelDetails) {
        return channelRepository.findById(id)
            .map(channel -> {
                channel.setName(channelDetails.getName());
                channel.setDescription(channelDetails.getDescription());
                channel.setIconUrl(channelDetails.getIconUrl());
                return channelRepository.save(channel);
            })
            .orElseThrow(() -> new RuntimeException("Channel not found with id: " + id));
    }
    
    public void deleteChannel(String id) {
        if (!channelRepository.existsById(id)) {
            throw new RuntimeException("Channel not found with id: " + id);
        }
        channelRepository.deleteById(id);
    }
    
    public boolean channelExists(String id) {
        return channelRepository.existsById(id);
    }
    
    public void deleteAllChannels() {
        channelRepository.deleteAll();
    }
}