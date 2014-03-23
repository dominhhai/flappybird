//
//  PlayScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/14/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import "PlayScene.h"
#import "GameConfig.h"
#import "Bird.h"
#import "Pipe.h"

@implementation PlayScene

+ (PlayScene *)scene {
    return [[self alloc] init];
}
- (id)init {
    self = [super init];
    if (!self) return(nil);
    
    [self setUserInteractionEnabled:YES];
    [self setMultipleTouchEnabled:NO];
    
    NSMutableDictionary* atlasInfo = GameConfig.atlasInfo;
    CCTexture *atlas = GameConfig.atlas;
    
    NSArray *bginfo = [atlasInfo objectForKey:@"bg_day"];
    CCSprite *background = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[bginfo objectAtIndex:3] intValue], [[bginfo objectAtIndex:4] intValue], [[bginfo objectAtIndex:1] intValue], [[bginfo objectAtIndex:2] intValue])];
    background.position = ccp(self.contentSize.width / 2, self.contentSize.height / 2);
    float scaleX = self.contentSize.width / background.contentSize.width;
    float scaleY = self.contentSize.height / background.contentSize.height;
    background.scaleX = scaleX;
    background.scaleY = scaleY;
    [self addChild:background];

    
    return self;
}

@end
