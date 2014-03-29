//
//  Pipe.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/21/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import "Pipe.h"
#import "PlayScene.h"
#import "GameConfig.h"

@implementation Pipe

@synthesize scene, type, sprTop, sprBottom;

bool birdPass = NO;
PlayScene * playScene;
float BIRD_X;

-(id)initWithType:(PipeType)pType position:(PipePosition)position scene:(CCScene*)pScene birdPos:(float) pBirdX {
    self = [super init];
    if(!self) return nil;
    
    self.scene = pScene;
    self.type = pType;
    BIRD_X = pBirdX;
    
    playScene = (PlayScene*) pScene;
    
    NSMutableDictionary* atlasInfo = GameConfig.atlasInfo;
    CCTexture *atlas = GameConfig.atlas;
    
    NSString* resource = (pType == PIPE_BLUE) ? @"pipe" : @"pipe2";
    NSArray *downInfo = [atlasInfo objectForKey:[NSString stringWithFormat:@"%@_down", resource]];
    NSArray *upInfo = [atlasInfo objectForKey:[NSString stringWithFormat:@"%@_up", resource]];
    
    self.sprTop = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[downInfo objectAtIndex:3] intValue], [[downInfo objectAtIndex:4] intValue], [[downInfo objectAtIndex:1] intValue], [[downInfo objectAtIndex:2] intValue])];
    self.sprBottom = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[upInfo objectAtIndex:3] intValue], [[upInfo objectAtIndex:4] intValue], [[upInfo objectAtIndex:1] intValue], [[upInfo objectAtIndex:2] intValue])];
    
    [self setPosition:position];
    
    [self.scene addChild:self.sprTop];
    [self.scene addChild:self.sprBottom];

    birdPass = NO;
    
    return self;
}

-(void) setPosition:(PipePosition) position {
    self.sprTop.position = ccp(position.x, position.top + self.sprTop.contentSize.height / 2);
    self.sprBottom.position = ccp(position.x, position.top - position.range - self.sprTop.contentSize.height / 2);
}

-(void) update:(CCTime)delta {
    CGPoint topPos = self.sprTop.position;
    
    if (topPos.x < -sprTop.contentSize.width) {
        PipePosition pos = [playScene genPipePosition:sprTop.contentSize.height];
        [self setPosition:pos];
        birdPass = NO;
        
        return;
    } else {
        CGPoint bottomPos = self.sprBottom.position;
        if (topPos.x < BIRD_X && !birdPass) {
            [playScene increaseScore];
            birdPass = YES;
        }
        topPos.x -= 1;
        bottomPos.x -= 1;
        self.sprTop.position = topPos;
        self.sprBottom.position = bottomPos;
    }
}

-(CGRect) getTopRect {
    CGSize size = CGSizeMake(self.sprTop.contentSize.width - 8, self.sprTop.contentSize.height - 8);
    CGPoint pos = ccp(self.sprTop.position.x - size.width / 2, self.sprTop.position.y - size.height / 2);
    CGRect rect = {pos, size};
    return rect;
}

-(CGRect) getBottomRect {
    CGSize size = CGSizeMake(self.sprBottom.contentSize.width - 8, self.sprBottom.contentSize.height - 8);
    CGPoint pos = ccp(self.sprBottom.position.x - size.width / 2, self.sprBottom.position.y - size.height / 2);
    CGRect rect = {pos, size};
    return rect;
}

@end
